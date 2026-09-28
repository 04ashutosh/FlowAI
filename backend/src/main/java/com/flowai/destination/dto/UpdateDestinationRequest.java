package com.flowai.destination.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateDestinationRequest(
        @NotBlank(message = "Name is required") String name,
        @NotBlank(message = "Host is required") String host,
        @NotNull(message = "Port is required")
        @Min(1) @Max(65535) Integer port,
        @NotBlank(message = "Database name is required") String databaseName,
        @NotBlank(message = "Username is required") String username,
        String password // Optional for update
) {}