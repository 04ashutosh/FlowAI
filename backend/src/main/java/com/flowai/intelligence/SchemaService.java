package com.flowai.intelligence;

import com.flowai.destination.Destination;
import com.flowai.source.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SchemaService {

    private static final Logger log = LoggerFactory.getLogger(SchemaService.class);

    public Map<String, List<String>> extractSourceSchema(Source source) {
        String jdbcUrl = source.getType().buildJdbcUrl(source.getHost(), source.getPort(), source.getDatabaseName());
        return extractSchema(jdbcUrl, source.getUsername(), source.getPassword());
    }

    public Map<String, List<String>> extractDestinationSchema(Destination destination) {
        String jdbcUrl = destination.getType().buildJdbcUrl(destination.getHost(), destination.getPort(), destination.getDatabaseName());
        return extractSchema(jdbcUrl, destination.getUsername(), destination.getPassword());
    }

    private Map<String, List<String>> extractSchema(String jdbcUrl, String username, String password) {
        Map<String, List<String>> schema = new HashMap<>();
        log.info("Extracting schema from {}", jdbcUrl);

        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            DatabaseMetaData metaData = connection.getMetaData();

            // Get all user tables. Pass null for schema to support both MySQL and Postgres gracefully.
            try (ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"})) {
                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");

                    // Skip internal system tables
                    if (tableName.startsWith("pg_") || tableName.startsWith("sql_")) {
                        continue;
                    }

                    List<String> columns = new ArrayList<>();
                    try (ResultSet cols = metaData.getColumns(null, null, tableName, "%")) {
                        while (cols.next()) {
                            String columnName = cols.getString("COLUMN_NAME");
                            String columnType = cols.getString("TYPE_NAME");
                            columns.add(columnName + " (" + columnType + ")");
                        }
                    }
                    schema.put(tableName, columns);
                }
            }
        } catch (Exception e) {
            log.error("Failed to extract schema from {}: {}", jdbcUrl, e.getMessage());
            throw new RuntimeException("Schema extraction failed. Verify database credentials and connection.", e);
        }

        return schema;
    }
}