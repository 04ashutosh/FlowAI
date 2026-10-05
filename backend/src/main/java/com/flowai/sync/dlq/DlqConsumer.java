package com.flowai.sync.dlq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flowai.pipeline.Pipeline;
import com.flowai.pipeline.PipelineRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DlqConsumer {
    private static final Logger logger = LoggerFactory.getLogger(DlqConsumer.class);

    private final DlqRecordRepository dlqRepo;
    private final PipelineRepository pipelineRepo;
    private final ObjectMapper jsonMapper;

    public DlqConsumer(DlqRecordRepository dlqRepo,PipelineRepository pipelineRepo,ObjectMapper jsonMapper){
        this.dlqRepo = dlqRepo;
        this.pipelineRepo = pipelineRepo;
        this.jsonMapper = jsonMapper;
    }

    @KafkaListener(topics = "flowai.sync.dlq",groupId = "flowai-dlq-group")
    @Transactional
    public void handleDlqEvent(com.flowai.sync.dlq.DlqEvent event){
        logger.warn("Received dead letter event for pipeline {}",event.pipelineId());

        try{
            Pipeline pipeline = pipelineRepo.findById(event.pipelineId())
                    .orElseThrow(()->new IllegalArgumentException("Pipeline missing"));

            String jsonPayloadString = jsonMapper.writeValueAsString(event.payload());

            DlqRecord record = new DlqRecord();
            record.setFailedPipeline(pipeline);
            record.setTargetTable(event.targetTable());
            record.setJsonPayload(jsonPayloadString);
            record.setFailureReason(event.failureReason());
            record.setResolved(false);

            dlqRepo.save(record);
            logger.info("Persisted DLQ record securely in database for later review.");
        }catch (Exception ex){
            logger.error("FATAL: Failed to save DLQ record: {}",ex.getMessage());
        }
    }
}
