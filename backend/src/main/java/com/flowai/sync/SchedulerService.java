package com.flowai.sync;

import com.flowai.pipeline.Pipeline;
import com.flowai.pipeline.PipelineRepository;
import com.flowai.pipeline.PipelineStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SchedulerService {

    private static final Logger log = LoggerFactory.getLogger(SchedulerService.class);
    private final PipelineRepository pipelineRepository;
    private final SyncEngineService syncEngineService;

    public SchedulerService(PipelineRepository pipelineRepository, SyncEngineService syncEngineService) {
        this.pipelineRepository = pipelineRepository;
        this.syncEngineService = syncEngineService;
    }

    // This Cron Job wakes up every 2 minutes.
    @Scheduled(fixedDelay = 120000)
    public void runAutomatedSyncs() {
        log.info("Cron Trigger: Checking for ACTIVE pipelines to sync...");

        List<Pipeline> activePipelines = pipelineRepository.findAll().stream()
                .filter(p -> p.getStatus() == PipelineStatus.ACTIVE)
                .toList();

        for (Pipeline pipeline : activePipelines) {
            log.info("Auto-syncing active pipeline: {}", pipeline.getId());
            try {
                // Triggers the exact same process as the manual sync button!
                syncEngineService.triggerSync(pipeline.getId());
            } catch (Exception e) {
                log.error("Scheduled sync failed for pipeline {}: {}", pipeline.getId(), e.getMessage());
            }
        }
    }
}