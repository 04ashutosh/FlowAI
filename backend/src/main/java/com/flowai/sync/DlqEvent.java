package com.flowai.sync.dlq;

import java.util.Map;
import java.util.UUID;

// Pure Java Record representing a failure event
public record DlqEvent(
        UUID pipelineId,
        String targetTable,
        Map<String, Object> payload,
        String failureReason
) {}