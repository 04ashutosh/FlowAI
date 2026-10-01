package com.flowai.pipeline;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PipelineRepository extends JpaRepository<Pipeline, UUID> {

    // Using an EntityGraph to fetch Source and Destination data efficiently in a single query
    @EntityGraph(attributePaths = {"source", "destination"})
    List<Pipeline> findAllByUserId(UUID userId);

    @EntityGraph(attributePaths = {"source", "destination"})
    Optional<Pipeline> findByIdAndUserId(UUID id, UUID userId);

    boolean existsByUserIdAndName(UUID userId, String name);
}