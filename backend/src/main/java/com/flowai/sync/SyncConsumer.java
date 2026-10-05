package com.flowai.sync;

import com.flowai.destination.Destination;
import com.flowai.pipeline.Pipeline;
import com.flowai.pipeline.PipelineRepository;
import com.flowai.sync.dlq.DlqEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SyncConsumer {

    private static final Logger logger = LoggerFactory.getLogger(SyncConsumer.class);
    private final PipelineRepository pipelineRepo;
    private final KafkaTemplate<String, Object> kafkaProducer;

    public SyncConsumer(PipelineRepository pipelineRepo, KafkaTemplate<String, Object> kafkaProducer) {
        this.pipelineRepo = pipelineRepo;
        this.kafkaProducer = kafkaProducer;
    }

    @KafkaListener(topics = "flowai.sync.events", groupId = "flowai-sync-group")
    @Transactional
    public void consumeSyncEvent(SyncEvent event) {
        logger.info("Processing event for pipeline: {}", event.pipelineId());

        Pipeline pipeline = pipelineRepo.findById(event.pipelineId())
                .orElseThrow(() -> new IllegalArgumentException("Pipeline not found"));

        Destination dest = pipeline.getDestination();
        String jdbcUrl = dest.getType().buildJdbcUrl(dest.getHost(), dest.getPort(), dest.getDatabaseName());

        try (Connection conn = DriverManager.getConnection(jdbcUrl, dest.getUsername(), dest.getPassword())) {

            Map<String, Object> payloadData = event.dataPayload();
            String columnNames = String.join(", ", payloadData.keySet());
            String placeholders = payloadData.keySet().stream().map(k -> "?").collect(Collectors.joining(", "));

            String insertSql = String.format("INSERT INTO %s (%s) VALUES (%s)", event.destinationTableName(), columnNames, placeholders);

            try (PreparedStatement preparedStmt = conn.prepareStatement(insertSql)) {
                int paramIndex = 1;
                for (Object val : payloadData.values()) {
                    preparedStmt.setObject(paramIndex++, val);
                }
                preparedStmt.executeUpdate();
                logger.info("Inserted mapped record into {}", event.destinationTableName());
            }

        } catch (Exception ex) {
            // ---> NEW: Send failed records to the Dead Letter Queue!
            logger.error("Sync failed! Routing to DLQ. Error: {}", ex.getMessage());

            DlqEvent dlqEvent = new DlqEvent(
                    event.pipelineId(),
                    event.destinationTableName(),
                    event.dataPayload(),
                    ex.getMessage()
            );

            kafkaProducer.send("flowai.sync.dlq", event.pipelineId().toString(), dlqEvent);
        }
    }
}