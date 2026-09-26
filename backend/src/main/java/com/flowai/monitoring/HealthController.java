package com.flowai.monitoring;

import com.flowai.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Custom health endpoint.
 *
 * NOTE: In Phase 2 we will configure Spring Security.
 * This endpoint must be publicly accessible (no auth required).
 * We will explicitly permit it in the SecurityConfig.
 */
@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, String>>> health() {
        var data = Map.of(
                "service", "FlowAI Backend",
                "status", "UP",
                "version", "0.0.1"
        );
        return ResponseEntity.ok(ApiResponse.success(data, "FlowAI is running"));
    }
}