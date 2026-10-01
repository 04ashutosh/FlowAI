package com.flowai.pipeline;

import com.flowai.common.response.ApiResponse;
import com.flowai.pipeline.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pipelines")
public class PipelineController {

    private final PipelineService pipelineService;

    public PipelineController(PipelineService pipelineService) {
        this.pipelineService = pipelineService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PipelineResponse>>> getAllPipelines() {
        return ResponseEntity.ok(ApiResponse.success(pipelineService.getAllPipelines(), "Pipelines retrieved"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PipelineResponse>> getPipelineById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(pipelineService.getPipelineById(id), "Pipeline retrieved"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PipelineResponse>> createPipeline(@Valid @RequestBody CreatePipelineRequest request) {
        PipelineResponse response = pipelineService.createPipeline(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Pipeline created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PipelineResponse>> updatePipeline(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePipelineRequest request) {
        PipelineResponse response = pipelineService.updatePipeline(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Pipeline updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePipeline(@PathVariable UUID id) {
        pipelineService.deletePipeline(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Pipeline deleted successfully"));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<PipelineResponse>> updateStatus(
            @PathVariable UUID id,
            @RequestParam PipelineStatus status) {
        PipelineResponse response = pipelineService.updateStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success(response, "Pipeline status updated successfully"));
    }
}