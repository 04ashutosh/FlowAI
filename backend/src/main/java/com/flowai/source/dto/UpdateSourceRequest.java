package com.flowai.source.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record UpdateSourceRequest(
        @NotBlank(message = "Source name is required")
        String name,

        @NotBlank(message = "Host is required")
        String host,

        @Min(value = 1, message = "Port must be greater than 0")
        @Max(value = 65535, message = "Port must be less than 65536")
        int port,

        @NotBlank(message = "Database name is required")
        String databaseName,

        @NotBlank(message = "Username is required")
        String username,

        // Optional on update: if null/blank, retain existing password
        String password
) {}