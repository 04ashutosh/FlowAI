package com.flowai.pipeline;

import com.flowai.auth.User;
import com.flowai.common.util.SecurityUtils;
import com.flowai.pipeline.dto.PipelineConfigResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class PipelineConfigService {

    private static final Logger log = LoggerFactory.getLogger(PipelineConfigService.class);

    private final PipelineConfigRepository configRepository;
    private final PipelineRepository pipelineRepository;

    public PipelineConfigService(PipelineConfigRepository configRepository, PipelineRepository pipelineRepository) {
        this.configRepository = configRepository;
        this.pipelineRepository = pipelineRepository;
    }

    @Transactional
    public PipelineConfigResponse saveConfig(UUID pipelineId, String mappingJson) {
        User user = SecurityUtils.getCurrentUser();

        Pipeline pipeline = pipelineRepository.findByIdAndUserId(pipelineId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Pipeline not found"));

        Optional<PipelineConfig> existingConfig = configRepository.findByPipelineId(pipelineId);
        PipelineConfig config = existingConfig.orElseGet(PipelineConfig::new);

        config.setPipeline(pipeline);
        config.setMappingJson(mappingJson);

        PipelineConfig saved = configRepository.save(config);
        log.info("Saved pipeline mapping configuration for pipeline {}", pipelineId);

        return PipelineConfigResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public PipelineConfigResponse getConfig(UUID pipelineId) {
        User user = SecurityUtils.getCurrentUser();

        pipelineRepository.findByIdAndUserId(pipelineId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Pipeline not found"));

        PipelineConfig config = configRepository.findByPipelineId(pipelineId)
                .orElseThrow(() -> new IllegalArgumentException("No mapping configuration found for this pipeline"));

        return PipelineConfigResponse.fromEntity(config);
    }
}