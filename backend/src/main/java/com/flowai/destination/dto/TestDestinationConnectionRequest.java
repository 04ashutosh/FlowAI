package com.flowai.destination.dto;

import com.flowai.destination.DestinationType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TestDestinationConnectionRequest(
        @NotNull(message = "Destination type is required") DestinationType type,
        @NotBlank(message = "Host is required") String host,
        @NotNull(message = "Port is required")
        @Min(1) @Max(65535) Integer port,
        @NotBlank(message = "Database name is required") String databaseName,
        @NotBlank(message = "Username is required") String username,
        @NotBlank(message = "Password is required") String password
) {}