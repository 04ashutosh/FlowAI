package com.flowai.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;

/**
 * Standard API response envelope for ALL FlowAI REST endpoints.
 *
 * Success:   { "success": true,  "data": {...},  "message": "...", "timestamp": "..." }
 * Error:     { "success": false, "data": null,   "message": "...", "timestamp": "..." }
 *
 * Why a wrapper?
 * - Consistent contract between frontend and backend.
 * - Frontend always knows where to find data vs errors.
 * - Adding metadata (pagination, request ID) later is non-breaking.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        String message,
        T data,
        Instant timestamp
) {

    /**
     * Factory method for successful responses with data.
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, message, data, Instant.now());
    }

    /**
     * Factory method for successful responses without a data body.
     * Example: DELETE operations.
     */
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message, null, Instant.now());
    }

    /**
     * Factory method for error responses.
     * Data is always null on error.
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null, Instant.now());
    }
}