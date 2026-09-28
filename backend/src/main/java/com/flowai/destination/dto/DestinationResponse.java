package com.flowai.destination.dto;

import com.flowai.destination.Destination;
import com.flowai.destination.DestinationType;

import java.time.Instant;
import java.util.UUID;

public record DestinationResponse(
        UUID id,
        String name,
        DestinationType type,
        String host,
        Integer port,
        String databaseName,
        String username,
        Instant createdAt,
        Instant updatedAt
) {
    public static DestinationResponse fromEntity(Destination destination) {
        return new DestinationResponse(
                destination.getId(),
                destination.getName(),
                destination.getType(),
                destination.getHost(),
                destination.getPort(),
                destination.getDatabaseName(),
                destination.getUsername(),
                destination.getCreatedAt(),
                destination.getUpdatedAt()
        );
    }
}