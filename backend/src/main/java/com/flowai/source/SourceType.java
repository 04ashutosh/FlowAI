package com.flowai.source;

public enum SourceType {
    POSTGRESQL("org.postgresql.Driver", "jdbc:postgresql://%s:%d/%s"),
    MYSQL("com.mysql.cj.jdbc.Driver", "jdbc:mysql://%s:%d/%s?useSSL=false&allowPublicKeyRetrieval=true");

    private final String driverClassName;
    private final String urlTemplate;

    SourceType(String driverClassName, String urlTemplate) {
        this.driverClassName = driverClassName;
        this.urlTemplate = urlTemplate;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public String buildJdbcUrl(String host, int port, String databaseName) {
        return String.format(urlTemplate, host, port, databaseName);
    }
}