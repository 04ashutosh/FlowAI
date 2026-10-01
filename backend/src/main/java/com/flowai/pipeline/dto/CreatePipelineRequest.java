package com.flowai.pipeline.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreatePipelineRequest(
        @NotBlank(message = "Name is required") String name,
        String description,
        @NotNull(message = "Source ID is required") UUID sourceId,
        @NotNull(message = "Destination ID is required") UUID destinationId
) {}