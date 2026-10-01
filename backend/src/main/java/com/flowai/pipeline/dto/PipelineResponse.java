package com.flowai.pipeline.dto;

import com.flowai.pipeline.Pipeline;
import com.flowai.pipeline.PipelineStatus;

import java.time.Instant;
import java.util.UUID;

public record PipelineResponse(
        UUID id,
        String name,
        String description,
        PipelineStatus status,
        UUID sourceId,
        String sourceName,
        UUID destinationId,
        String destinationName,
        Instant createdAt,
        Instant updatedAt
) {
    public static PipelineResponse fromEntity(Pipeline pipeline) {
        return new PipelineResponse(
                pipeline.getId(),
                pipeline.getName(),
                pipeline.getDescription(),
                pipeline.getStatus(),
                pipeline.getSource().getId(),
                pipeline.getSource().getName(),
                pipeline.getDestination().getId(),
                pipeline.getDestination().getName(),
                pipeline.getCreatedAt(),
                pipeline.getUpdatedAt()
        );
    }
}