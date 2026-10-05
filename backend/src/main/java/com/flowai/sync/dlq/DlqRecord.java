package com.flowai.sync.dlq;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flowai.pipeline.Pipeline;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "dead_letter_queue")
public class DlqRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // JsonIgnore prevents circular serialization issues when reading via REST later
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pipeline_id", nullable = false)
    @JsonIgnore
    private Pipeline failedPipeline;

    @Column(name = "target_table", nullable = false)
    private String targetTable;

    @Column(name = "json_payload", nullable = false, columnDefinition = "TEXT")
    private String jsonPayload;

    @Column(name = "failure_reason", nullable = false, columnDefinition = "TEXT")
    private String failureReason;

    @Column(name = "is_resolved", nullable = false)
    private boolean isResolved = false;

    @Column(name = "time_created", nullable = false, updatable = false)
    private Instant timeCreated;

    @Column(name = "time_updated", nullable = false)
    private Instant timeUpdated;

    @PrePersist
    protected void onPersist() {
        this.timeCreated = Instant.now();
        this.timeUpdated = Instant.now();
    }

    @PreUpdate
    protected void onMerge() {
        this.timeUpdated = Instant.now();
    }

    // --- Standard Getters and Setters ---

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Pipeline getFailedPipeline() { return failedPipeline; }
    public void setFailedPipeline(Pipeline failedPipeline) { this.failedPipeline = failedPipeline; }

    public String getTargetTable() { return targetTable; }
    public void setTargetTable(String targetTable) { this.targetTable = targetTable; }

    public String getJsonPayload() { return jsonPayload; }
    public void setJsonPayload(String jsonPayload) { this.jsonPayload = jsonPayload; }

    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }

    public boolean isResolved() { return isResolved; }
    public void setResolved(boolean isResolved) { this.isResolved = isResolved; }

    public Instant getTimeCreated() { return timeCreated; }
    public void setTimeCreated(Instant timeCreated) { this.timeCreated = timeCreated; }

    public Instant getTimeUpdated() { return timeUpdated; }
    public void setTimeUpdated(Instant timeUpdated) { this.timeUpdated = timeUpdated; }
}