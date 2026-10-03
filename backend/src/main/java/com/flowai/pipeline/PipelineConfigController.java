package com.flowai.pipeline;

import com.flowai.common.response.ApiResponse;
import com.flowai.pipeline.dto.PipelineConfigResponse;
import com.flowai.pipeline.dto.SavePipelineConfigRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/pipelines/{pipelineId}/config")
public class PipelineConfigController {

    private final PipelineConfigService configService;

    public PipelineConfigController(PipelineConfigService configService) {
        this.configService = configService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PipelineConfigResponse>> saveConfig(
            @PathVariable UUID pipelineId,
            @Valid @RequestBody SavePipelineConfigRequest request) {
        PipelineConfigResponse saved = configService.saveConfig(pipelineId, request.mappingJson());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(saved, "Pipeline mapping configuration saved successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PipelineConfigResponse>> getConfig(@PathVariable UUID pipelineId) {
        PipelineConfigResponse config = configService.getConfig(pipelineId);
        return ResponseEntity.ok(ApiResponse.success(config, "Pipeline mapping configuration retrieved"));
    }
}