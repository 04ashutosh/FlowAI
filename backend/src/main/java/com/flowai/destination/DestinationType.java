package com.flowai.destination;

public enum DestinationType {
    POSTGRESQL("jdbc:postgresql://%s:%d/%s"),
    MYSQL("jdbc:mysql://%s:%d/%s");

    private final String jdbcUrlTemplate;

    DestinationType(String jdbcUrlTemplate) {
        this.jdbcUrlTemplate = jdbcUrlTemplate;
    }

    public String buildJdbcUrl(String host, int port, String databaseName) {
        return String.format(jdbcUrlTemplate, host, port, databaseName);
    }
}