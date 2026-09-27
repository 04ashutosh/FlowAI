package com.flowai.source;

import com.flowai.common.response.ApiResponse;
import com.flowai.source.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/sources")
public class SourceController {

    private final SourceService sourceService;

    public SourceController(SourceService sourceService) {
        this.sourceService = sourceService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SourceResponse>>> getAllSources() {
        return ResponseEntity.ok(ApiResponse.success(sourceService.getAllSources(), "Sources retrieved"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SourceResponse>> getSourceById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(sourceService.getSourceById(id), "Source retrieved"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SourceResponse>> createSource(@Valid @RequestBody CreateSourceRequest request) {
        SourceResponse response = sourceService.createSource(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Source created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SourceResponse>> updateSource(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateSourceRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(sourceService.updateSource(id, request), "Source updated"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSource(@PathVariable UUID id) {
        sourceService.deleteSource(id);
        return ResponseEntity.ok(ApiResponse.success("Source deleted successfully"));
    }

    @PostMapping("/test")
    public ResponseEntity<ApiResponse<ConnectionTestResult>> testConnection(
            @Valid @RequestBody TestConnectionRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(sourceService.testConnection(request), "Connection test completed"));
    }

    @PostMapping("/{id}/test")
    public ResponseEntity<ApiResponse<ConnectionTestResult>> testSavedConnection(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(sourceService.testSavedSourceConnection(id), "Connection test completed"));
    }
}