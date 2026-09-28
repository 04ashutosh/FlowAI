package com.flowai.destination;

import com.flowai.common.response.ApiResponse;
import com.flowai.destination.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/destinations")
public class DestinationController {

    private final DestinationService destinationService;

    public DestinationController(DestinationService destinationService) {
        this.destinationService = destinationService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DestinationResponse>>> getAllDestinations() {
        return ResponseEntity.ok(ApiResponse.success(destinationService.getAllDestinations(), "Destinations retrieved"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DestinationResponse>> getDestinationById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(destinationService.getDestinationById(id), "Destination retrieved"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DestinationResponse>> createDestination(@Valid @RequestBody CreateDestinationRequest request) {
        DestinationResponse response = destinationService.createDestination(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Destination created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DestinationResponse>> updateDestination(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateDestinationRequest request) {
        DestinationResponse response = destinationService.updateDestination(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Destination updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDestination(@PathVariable UUID id) {
        destinationService.deleteDestination(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Destination deleted successfully"));
    }

    @PostMapping("/test")
    public ResponseEntity<ApiResponse<DestinationConnectionTestResult>> testConnection(
            @Valid @RequestBody TestDestinationConnectionRequest request) {
        DestinationConnectionTestResult result = destinationService.testConnection(request);
        return ResponseEntity.ok(ApiResponse.success(result, "Connection test completed"));
    }

    @PostMapping("/{id}/test")
    public ResponseEntity<ApiResponse<DestinationConnectionTestResult>> testSavedDestinationConnection(@PathVariable UUID id) {
        DestinationConnectionTestResult result = destinationService.testSavedDestinationConnection(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Connection test completed"));
    }
}