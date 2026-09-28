package com.flowai.destination.dto;

public record DestinationConnectionTestResult(
        boolean success,
        String message,
        long durationMs
) {
    public static DestinationConnectionTestResult success(long durationMs) {
        return new DestinationConnectionTestResult(true, "Connection successful", durationMs);
    }

    public static DestinationConnectionTestResult failure(String message, long durationMs) {
        return new DestinationConnectionTestResult(false, message, durationMs);
    }
}