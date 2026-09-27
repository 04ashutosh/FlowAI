package com.flowai.source.dto;

import com.flowai.source.SourceType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TestConnectionRequest(
        @NotNull(message = "Source type is required")
        SourceType type,

        @NotBlank(message = "Host is required")
        String host,

        @Min(1) @Max(65535)
        int port,

        @NotBlank(message = "Database name is required")
        String databaseName,

        @NotBlank(message = "Username is required")
        String username,

        @NotBlank(message = "Password is required")
        String password
) {}