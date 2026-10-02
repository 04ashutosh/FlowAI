package com.flowai.sync;

import java.util.Map;
import java.util.UUID;

// Pure Java Record DTO for Kafka messages
public record SyncEvent(
        UUID pipelineId,
        String destinationTableName,
        Map<String, Object> dataPayload
) {}