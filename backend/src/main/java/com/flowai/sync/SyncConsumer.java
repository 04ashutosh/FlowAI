package com.flowai.sync;

import com.flowai.destination.Destination;
import com.flowai.pipeline.Pipeline;
import com.flowai.pipeline.PipelineRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SyncConsumer {

    private static final Logger log = LoggerFactory.getLogger(SyncConsumer.class);
    private final PipelineRepository pipelineRepository;

    public SyncConsumer(PipelineRepository pipelineRepository) {
        this.pipelineRepository = pipelineRepository;
    }

    @KafkaListener(topics = "flowai.sync.events", groupId = "flowai-sync-group")
    @Transactional
    public void consumeSyncEvent(SyncEvent event) {
        log.info("Consumer received sync event for pipeline: {}", event.pipelineId());

        Pipeline pipeline = pipelineRepository.findById(event.pipelineId())
                .orElseThrow(() -> new IllegalArgumentException("Pipeline not found"));

        Destination dest = pipeline.getDestination();
        String jdbcUrl = dest.getType().buildJdbcUrl(dest.getHost(), dest.getPort(), dest.getDatabaseName());

        try (Connection connection = DriverManager.getConnection(jdbcUrl, dest.getUsername(), dest.getPassword())) {

            Map<String, Object> data = event.dataPayload();
            String columns = String.join(", ", data.keySet());
            String placeholders = data.keySet().stream().map(k -> "?").collect(Collectors.joining(", "));

            String sql = String.format("INSERT INTO %s (%s) VALUES (%s)", event.destinationTableName(), columns, placeholders);

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                int index = 1;
                for (Object value : data.values()) {
                    stmt.setObject(index++, value);
                }
                stmt.executeUpdate();
                log.info("Successfully inserted record into destination table '{}'", event.destinationTableName());
            }

        } catch (Exception e) {
            log.error("Failed to insert synced data: {}", e.getMessage());
            // In a production environment, we would route this to a Dead Letter Queue (DLQ)
        }
    }
}