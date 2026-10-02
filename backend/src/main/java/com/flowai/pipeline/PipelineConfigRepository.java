package com.flowai.pipeline;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PipelineConfigRepository extends JpaRepository<PipelineConfig, UUID> {
    Optional<PipelineConfig> findByPipelineId(UUID pipelineId);
}