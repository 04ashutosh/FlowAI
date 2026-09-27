package com.flowai.source;

import com.flowai.auth.User;
import com.flowai.common.util.SecurityUtils;
import com.flowai.source.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class SourceService {

    private static final Logger log = LoggerFactory.getLogger(SourceService.class);
    private static final int CONNECTION_TIMEOUT_SECONDS = 5;

    private final SourceRepository sourceRepository;

    public SourceService(SourceRepository sourceRepository) {
        this.sourceRepository = sourceRepository;
    }

    @Transactional(readOnly = true)
    public List<SourceResponse> getAllSources() {
        User user = SecurityUtils.getCurrentUser();
        return sourceRepository.findAllByUserId(user.getId())
                .stream()
                .map(SourceResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public SourceResponse getSourceById(UUID id) {
        User user = SecurityUtils.getCurrentUser();
        Source source = sourceRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Source not found with ID: " + id));
        return SourceResponse.fromEntity(source);
    }

    @Transactional
    public SourceResponse createSource(CreateSourceRequest request) {
        User user = SecurityUtils.getCurrentUser();

        if (sourceRepository.existsByUserIdAndName(user.getId(), request.name())) {
            throw new IllegalArgumentException("A source with this name already exists");
        }

        Source source = new Source();
        source.setUser(user);
        source.setName(request.name());
        source.setType(request.type());
        source.setHost(request.host());
        source.setPort(request.port());
        source.setDatabaseName(request.databaseName());
        source.setUsername(request.username());
        source.setPassword(request.password());

        Source saved = sourceRepository.save(source);
        log.info("Created new source '{}' (type: {}) for user {}", saved.getName(), saved.getType(), user.getEmail());
        return SourceResponse.fromEntity(saved);
    }

    @Transactional
    public SourceResponse updateSource(UUID id, UpdateSourceRequest request) {
        User user = SecurityUtils.getCurrentUser();
        Source source = sourceRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Source not found with ID: " + id));

        source.setName(request.name());
        source.setHost(request.host());
        source.setPort(request.port());
        source.setDatabaseName(request.databaseName());
        source.setUsername(request.username());
        if (request.password() != null && !request.password().isBlank()) {
            source.setPassword(request.password());
        }
        source.setUpdatedAt(Instant.now());

        Source updated = sourceRepository.save(source);
        log.info("Updated source '{}' for user {}", updated.getName(), user.getEmail());
        return SourceResponse.fromEntity(updated);
    }

    @Transactional
    public void deleteSource(UUID id) {
        User user = SecurityUtils.getCurrentUser();
        Source source = sourceRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Source not found with ID: " + id));

        sourceRepository.delete(source);
        log.info("Deleted source '{}' for user {}", source.getName(), user.getEmail());
    }

    public ConnectionTestResult testConnection(TestConnectionRequest request) {
        String jdbcUrl = request.type().buildJdbcUrl(request.host(), request.port(), request.databaseName());
        return executePing(jdbcUrl, request.username(), request.password());
    }

    public ConnectionTestResult testSavedSourceConnection(UUID id) {
        User user = SecurityUtils.getCurrentUser();
        Source source = sourceRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Source not found with ID: " + id));

        String jdbcUrl = source.getType().buildJdbcUrl(source.getHost(), source.getPort(), source.getDatabaseName());
        return executePing(jdbcUrl, source.getUsername(), source.getPassword());
    }

    private ConnectionTestResult executePing(String jdbcUrl, String username, String password) {
        long start = System.currentTimeMillis();
        DriverManager.setLoginTimeout(CONNECTION_TIMEOUT_SECONDS);

        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            boolean valid = connection.isValid(CONNECTION_TIMEOUT_SECONDS);
            long duration = System.currentTimeMillis() - start;
            if (valid) {
                return ConnectionTestResult.success(duration);
            } else {
                return ConnectionTestResult.failure("Connection check returned invalid", duration);
            }
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - start;
            log.warn("Connection test failed for URL {}: {}", jdbcUrl, e.getMessage());
            return ConnectionTestResult.failure(e.getMessage(), duration);
        }
    }
}