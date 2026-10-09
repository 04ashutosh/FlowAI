package com.flowai.sync.dlq;

import com.flowai.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/dlq")
public class DlqController {

    private final DlqRecordRepository dlqRepository;

    public DlqController(DlqRecordRepository dlqRepository) {
        this.dlqRepository = dlqRepository;
    }

    @GetMapping("/pipeline/{pipelineId}")
    public ResponseEntity<ApiResponse<List<DlqRecord>>> getDlqRecords(@PathVariable UUID pipelineId) {
        List<DlqRecord> records = dlqRepository.findByFailedPipelineId(pipelineId);
        return ResponseEntity.ok(ApiResponse.success(records, "DLQ records retrieved"));
    }
}