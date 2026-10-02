package com.flowai.pipeline;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "pipeline_configs")
public class PipelineConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pipeline_id", nullable = false, unique = true)
    private Pipeline pipeline;

    @Column(name = "mapping_json", nullable = false, columnDefinition = "TEXT")
    private String mappingJson;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Pipeline getPipeline() { return pipeline; }
    public void setPipeline(Pipeline pipeline) { this.pipeline = pipeline; }
    public String getMappingJson() { return mappingJson; }
    public void setMappingJson(String mappingJson) { this.mappingJson = mappingJson; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}