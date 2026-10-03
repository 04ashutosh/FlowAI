package com.flowai.sync;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flowai.pipeline.Pipeline;
import com.flowai.pipeline.PipelineConfig;
import com.flowai.pipeline.PipelineConfigRepository;
import com.flowai.pipeline.PipelineRepository;
import com.flowai.source.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class SyncEngineService {

    private static final Logger log = LoggerFactory.getLogger(SyncEngineService.class);

    private final PipelineRepository pipelineRepository;
    private final PipelineConfigRepository configRepository;
    private final SyncProducer syncProducer;
    private final ObjectMapper objectMapper;

    public SyncEngineService(PipelineRepository pipelineRepository,
                             PipelineConfigRepository configRepository,
                             SyncProducer syncProducer,
                             ObjectMapper objectMapper) {
        this.pipelineRepository = pipelineRepository;
        this.configRepository = configRepository;
        this.syncProducer = syncProducer;
        this.objectMapper = objectMapper;
    }

    public void triggerSync(UUID pipelineId) {
        Pipeline pipeline = pipelineRepository.findById(pipelineId)
                .orElseThrow(() -> new IllegalArgumentException("Pipeline not found"));

        PipelineConfig config = configRepository.findByPipelineId(pipelineId)
                .orElseThrow(() -> new IllegalArgumentException("Pipeline mapping configuration not found"));

        try {
            // Expected mappingJson format from Ollama: { "dest_table": { "dest_col": "source_col" } }
            Map<String, Map<String, String>> mappings = objectMapper.readValue(
                    config.getMappingJson(), new TypeReference<>() {});

            Source source = pipeline.getSource();
            String sourceUrl = source.getType().buildJdbcUrl(source.getHost(), source.getPort(), source.getDatabaseName());

            log.info("Starting sync for pipeline {}. Connecting to source {}", pipelineId, sourceUrl);

            try (Connection conn = DriverManager.getConnection(sourceUrl, source.getUsername(), source.getPassword());
                 Statement stmt = conn.createStatement()) {

                for (Map.Entry<String, Map<String, String>> entry : mappings.entrySet()) {
                    String destTable = entry.getKey();
                    Map<String, String> fieldMap = entry.getValue();

                    // Heuristic: Extract the source table name from the first mapped column if available
                    String aSourceCol = fieldMap.values().iterator().next();
                    String sourceTable = aSourceCol.contains(".") ? aSourceCol.split("\\.")[0] : destTable;

                    log.info("Querying source table '{}' to map to destination table '{}'", sourceTable, destTable);

                    ResultSet rs = stmt.executeQuery("SELECT * FROM " + sourceTable);
                    int count = 0;

                    while (rs.next()) {
                        Map<String, Object> payload = new HashMap<>();

                        // Map the physical source row to the logical destination schema
                        for (Map.Entry<String, String> fieldEntry : fieldMap.entrySet()) {
                            String destCol = fieldEntry.getKey();
                            String srcCol = fieldEntry.getValue().contains(".") ?
                                    fieldEntry.getValue().split("\\.")[1] : fieldEntry.getValue();

                            Object value = rs.getObject(srcCol);
                            payload.put(destCol, value);
                        }

                        // Send the mapped payload to Kafka asynchronously!
                        SyncEvent event = new SyncEvent(pipelineId, destTable, payload);
                        syncProducer.sendSyncEvent(event);
                        count++;
                    }
                    log.info("Finished querying source. Sent {} records to Kafka for destination table '{}'", count, destTable);
                }
            }
        } catch (Exception e) {
            log.error("Failed to execute sync for pipeline {}: {}", pipelineId, e.getMessage());
            throw new RuntimeException("Sync execution failed: " + e.getMessage(), e);
        }
    }
}