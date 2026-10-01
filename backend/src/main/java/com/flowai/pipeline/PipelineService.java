package com.flowai.pipeline;

import com.flowai.auth.User;
import com.flowai.common.util.SecurityUtils;
import com.flowai.destination.Destination;
import com.flowai.destination.DestinationRepository;
import com.flowai.pipeline.dto.CreatePipelineRequest;
import com.flowai.pipeline.dto.PipelineResponse;
import com.flowai.pipeline.dto.UpdatePipelineRequest;
import com.flowai.source.Source;
import com.flowai.source.SourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class PipelineService {

    private static final Logger log = LoggerFactory.getLogger(PipelineService.class);

    private final PipelineRepository pipelineRepository;
    private final SourceRepository sourceRepository;
    private final DestinationRepository destinationRepository;

    public PipelineService(PipelineRepository pipelineRepository,
                           SourceRepository sourceRepository,
                           DestinationRepository destinationRepository) {
        this.pipelineRepository = pipelineRepository;
        this.sourceRepository = sourceRepository;
        this.destinationRepository = destinationRepository;
    }

    @Transactional(readOnly = true)
    public List<PipelineResponse> getAllPipelines() {
        User user = SecurityUtils.getCurrentUser();
        return pipelineRepository.findAllByUserId(user.getId())
                .stream()
                .map(PipelineResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public PipelineResponse getPipelineById(UUID id) {
        User user = SecurityUtils.getCurrentUser();
        Pipeline pipeline = pipelineRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Pipeline not found with ID: " + id));
        return PipelineResponse.fromEntity(pipeline);
    }

    @Transactional
    public PipelineResponse createPipeline(CreatePipelineRequest request) {
        User user = SecurityUtils.getCurrentUser();

        if (pipelineRepository.existsByUserIdAndName(user.getId(), request.name())) {
            throw new IllegalArgumentException("A pipeline with this name already exists");
        }

        // Validate that both source and destination belong to the current user
        Source source = sourceRepository.findByIdAndUserId(request.sourceId(), user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Source not found"));

        Destination destination = destinationRepository.findByIdAndUserId(request.destinationId(), user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Destination not found"));

        Pipeline pipeline = new Pipeline();
        pipeline.setUser(user);
        pipeline.setName(request.name());
        pipeline.setDescription(request.description());
        pipeline.setSource(source);
        pipeline.setDestination(destination);

        Pipeline saved = pipelineRepository.save(pipeline);
        log.info("Created pipeline '{}' for user {}", saved.getName(), user.getEmail());
        return PipelineResponse.fromEntity(saved);
    }

    @Transactional
    public PipelineResponse updatePipeline(UUID id, UpdatePipelineRequest request) {
        User user = SecurityUtils.getCurrentUser();
        Pipeline pipeline = pipelineRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Pipeline not found with ID: " + id));

        pipeline.setName(request.name());
        pipeline.setDescription(request.description());
        pipeline.setUpdatedAt(Instant.now());

        Pipeline updated = pipelineRepository.save(pipeline);
        log.info("Updated pipeline '{}' for user {}", updated.getName(), user.getEmail());
        return PipelineResponse.fromEntity(updated);
    }

    @Transactional
    public void deletePipeline(UUID id) {
        User user = SecurityUtils.getCurrentUser();
        Pipeline pipeline = pipelineRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Pipeline not found with ID: " + id));

        pipelineRepository.delete(pipeline);
        log.info("Deleted pipeline '{}' for user {}", pipeline.getName(), user.getEmail());
    }

    @Transactional
    public PipelineResponse updateStatus(UUID id, PipelineStatus status) {
        User user = SecurityUtils.getCurrentUser();
        Pipeline pipeline = pipelineRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Pipeline not found with ID: " + id));

        pipeline.setStatus(status);
        pipeline.setUpdatedAt(Instant.now());

        Pipeline updated = pipelineRepository.save(pipeline);
        log.info("Updated pipeline '{}' status to {} for user {}", updated.getName(), status, user.getEmail());
        return PipelineResponse.fromEntity(updated);
    }
}