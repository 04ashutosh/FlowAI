package com.flowai.sync;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class SyncProducer {

    private static final Logger log = LoggerFactory.getLogger(SyncProducer.class);
    private static final String TOPIC = "flowai.sync.events";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public SyncProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendSyncEvent(SyncEvent event) {
        log.info("Sending sync event for pipeline: {} to destination table: {}", event.pipelineId(), event.destinationTableName());
        kafkaTemplate.send(TOPIC, event.pipelineId().toString(), event);
    }
}