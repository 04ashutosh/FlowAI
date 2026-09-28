package com.flowai.destination;

import com.flowai.auth.User;
import com.flowai.common.util.SecurityUtils;
import com.flowai.destination.dto.*;
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
public class DestinationService {

    private static final Logger log = LoggerFactory.getLogger(DestinationService.class);
    private static final int CONNECTION_TIMEOUT_SECONDS = 5;

    private final DestinationRepository destinationRepository;

    public DestinationService(DestinationRepository destinationRepository) {
        this.destinationRepository = destinationRepository;
    }

    @Transactional(readOnly = true)
    public List<DestinationResponse> getAllDestinations() {
        User user = SecurityUtils.getCurrentUser();
        return destinationRepository.findAllByUserId(user.getId())
                .stream()
                .map(DestinationResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public DestinationResponse getDestinationById(UUID id) {
        User user = SecurityUtils.getCurrentUser();
        Destination destination = destinationRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Destination not found with ID: " + id));
        return DestinationResponse.fromEntity(destination);
    }

    @Transactional
    public DestinationResponse createDestination(CreateDestinationRequest request) {
        User user = SecurityUtils.getCurrentUser();

        if (destinationRepository.existsByUserIdAndName(user.getId(), request.name())) {
            throw new IllegalArgumentException("A destination with this name already exists");
        }

        Destination destination = new Destination();
        destination.setUser(user);
        destination.setName(request.name());
        destination.setType(request.type());
        destination.setHost(request.host());
        destination.setPort(request.port());
        destination.setDatabaseName(request.databaseName());
        destination.setUsername(request.username());
        destination.setPassword(request.password());

        Destination saved = destinationRepository.save(destination);
        log.info("Created new destination '{}' for user {}", saved.getName(), user.getEmail());
        return DestinationResponse.fromEntity(saved);
    }

    @Transactional
    public DestinationResponse updateDestination(UUID id, UpdateDestinationRequest request) {
        User user = SecurityUtils.getCurrentUser();
        Destination destination = destinationRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Destination not found with ID: " + id));

        destination.setName(request.name());
        destination.setHost(request.host());
        destination.setPort(request.port());
        destination.setDatabaseName(request.databaseName());
        destination.setUsername(request.username());
        if (request.password() != null && !request.password().isBlank()) {
            destination.setPassword(request.password());
        }
        destination.setUpdatedAt(Instant.now());

        Destination updated = destinationRepository.save(destination);
        log.info("Updated destination '{}' for user {}", updated.getName(), user.getEmail());
        return DestinationResponse.fromEntity(updated);
    }

    @Transactional
    public void deleteDestination(UUID id) {
        User user = SecurityUtils.getCurrentUser();
        Destination destination = destinationRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Destination not found with ID: " + id));

        destinationRepository.delete(destination);
        log.info("Deleted destination '{}' for user {}", destination.getName(), user.getEmail());
    }

    public DestinationConnectionTestResult testConnection(TestDestinationConnectionRequest request) {
        String jdbcUrl = request.type().buildJdbcUrl(request.host(), request.port(), request.databaseName());
        return executePing(jdbcUrl, request.username(), request.password());
    }

    public DestinationConnectionTestResult testSavedDestinationConnection(UUID id) {
        User user = SecurityUtils.getCurrentUser();
        Destination destination = destinationRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Destination not found with ID: " + id));

        String jdbcUrl = destination.getType().buildJdbcUrl(destination.getHost(), destination.getPort(), destination.getDatabaseName());
        return executePing(jdbcUrl, destination.getUsername(), destination.getPassword());
    }

    private DestinationConnectionTestResult executePing(String jdbcUrl, String username, String password) {
        long start = System.currentTimeMillis();
        DriverManager.setLoginTimeout(CONNECTION_TIMEOUT_SECONDS);

        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            boolean valid = connection.isValid(CONNECTION_TIMEOUT_SECONDS);
            long duration = System.currentTimeMillis() - start;
            if (valid) {
                return DestinationConnectionTestResult.success(duration);
            } else {
                return DestinationConnectionTestResult.failure("Connection check returned invalid", duration);
            }
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - start;
            log.warn("Destination connection test failed for URL {}: {}", jdbcUrl, e.getMessage());
            return DestinationConnectionTestResult.failure(e.getMessage(), duration);
        }
    }
}