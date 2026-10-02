package com.flowai.intelligence;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flowai.auth.User;
import com.flowai.common.response.ApiResponse;
import com.flowai.common.util.SecurityUtils;
import com.flowai.pipeline.Pipeline;
import com.flowai.pipeline.PipelineRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/intelligence")
public class IntelligenceController {

    private final PipelineRepository pipelineRepository;
    private final SchemaService schemaService;
    private final OllamaService ollamaService;
    private final ObjectMapper objectMapper;

    public IntelligenceController(PipelineRepository pipelineRepository,
                                  SchemaService schemaService,
                                  OllamaService ollamaService,
                                  ObjectMapper objectMapper) {
        this.pipelineRepository = pipelineRepository;
        this.schemaService = schemaService;
        this.ollamaService = ollamaService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/pipelines/{pipelineId}/auto-map")
    public ResponseEntity<ApiResponse<JsonNode>> autoMapPipeline(@PathVariable UUID pipelineId) {
        User user = SecurityUtils.getCurrentUser();

        Pipeline pipeline = pipelineRepository.findByIdAndUserId(pipelineId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Pipeline not found"));

        // 1. Extract physical schemas dynamically
        Map<String, java.util.List<String>> sourceSchema = schemaService.extractSourceSchema(pipeline.getSource());
        Map<String, java.util.List<String>> destSchema = schemaService.extractDestinationSchema(pipeline.getDestination());

        // 2. Ask Ollama to map them
        String jsonMappingString = ollamaService.generateMapping(sourceSchema, destSchema);

        // 3. Return the generated JSON to the user for review
        try {
            JsonNode mappingJson = objectMapper.readTree(jsonMappingString);
            return ResponseEntity.ok(ApiResponse.success(mappingJson, "AI mapping generated successfully"));
        } catch (Exception e) {
            throw new RuntimeException("AI returned invalid JSON: " + jsonMappingString, e);
        }
    }
}