package com.flowai.source.dto;

public record ConnectionTestResult(
        boolean connected,
        String message,
        long responseTimeMs
) {
    public static ConnectionTestResult success(long responseTimeMs) {
        return new ConnectionTestResult(true, "Connection successful", responseTimeMs);
    }

    public static ConnectionTestResult failure(String errorMessage, long responseTimeMs) {
        return new ConnectionTestResult(false, errorMessage, responseTimeMs);
    }
}