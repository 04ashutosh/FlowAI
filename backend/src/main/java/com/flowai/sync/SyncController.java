package com.flowai.sync;

import com.flowai.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/sync")
public class SyncController {

    private final SyncEngineService syncEngineService;

    public SyncController(SyncEngineService syncEngineService) {
        this.syncEngineService = syncEngineService;
    }

    @PostMapping("/{pipelineId}/trigger")
    public ResponseEntity<ApiResponse<Void>> triggerSync(@PathVariable UUID pipelineId) {
        // In a production system, this would simply drop a message onto a queue to be processed asynchronously by a worker node.
        // For our MVP, we will execute it synchronously so we can see it happen instantly.
        syncEngineService.triggerSync(pipelineId);
        return ResponseEntity.ok(ApiResponse.success(null, "Sync triggered successfully and events dispatched to Kafka"));
    }
}