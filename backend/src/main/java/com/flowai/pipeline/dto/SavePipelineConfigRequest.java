package com.flowai.pipeline.dto;

import jakarta.validation.constraints.NotBlank;

public record SavePipelineConfigRequest(
        @NotBlank(message = "Mapping JSON is required")
        String mappingJson
) {}