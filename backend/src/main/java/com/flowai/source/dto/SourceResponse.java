package com.flowai.source.dto;

import com.flowai.source.Source;
import com.flowai.source.SourceType;

import java.time.Instant;
import java.util.UUID;

public record SourceResponse(
        UUID id,
        String name,
        SourceType type,
        String host,
        int port,
        String databaseName,
        String username,
        Instant createdAt,
        Instant updatedAt
) {
    public static SourceResponse fromEntity(Source source) {
        return new SourceResponse(
                source.getId(),
                source.getName(),
                source.getType(),
                source.getHost(),
                source.getPort(),
                source.getDatabaseName(),
                source.getUsername(),
                source.getCreatedAt(),
                source.getUpdatedAt()
        );
    }
}