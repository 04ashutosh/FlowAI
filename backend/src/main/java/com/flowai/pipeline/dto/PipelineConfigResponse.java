package com.flowai.pipeline.dto;

import com.flowai.pipeline.PipelineConfig;

import java.time.Instant;
import java.util.UUID;

// We use a Response DTO to avoid circular JSON serialization issues with the Pipeline entity
public record PipelineConfigResponse(
        UUID id,
        UUID pipelineId,
        String mappingJson,
        Instant createdAt,
        Instant updatedAt
) {
    public static PipelineConfigResponse fromEntity(PipelineConfig config) {
        return new PipelineConfigResponse(
                config.getId(),
                config.getPipeline().getId(),
                config.getMappingJson(),
                config.getCreatedAt(),
                config.getUpdatedAt()
        );
    }
}