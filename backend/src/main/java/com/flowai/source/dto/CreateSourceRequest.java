package com.flowai.source.dto;

import com.flowai.source.SourceType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateSourceRequest(
        @NotBlank(message = "Source name is required")
        String name,

        @NotNull(message = "Source type is required (POSTGRESQL or MYSQL)")
        SourceType type,

        @NotBlank(message = "Host is required")
        String host,

        @Min(value = 1, message = "Port must be greater than 0")
        @Max(value = 65535, message = "Port must be less than 65536")
        int port,

        @NotBlank(message = "Database name is required")
        String databaseName,

        @NotBlank(message = "Username is required")
        String username,

        @NotBlank(message = "Password is required")
        String password
) {}