package com.flowai.intelligence;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class OllamaService {

    private static final Logger log = LoggerFactory.getLogger(OllamaService.class);
    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String modelName;

    public OllamaService(
            ObjectMapper objectMapper,
            @Value("${ollama.url:http://localhost:11434}") String ollamaUrl,
            @Value("${ollama.model:llama3.2}") String modelName
    ) {
        this.objectMapper = objectMapper;
        this.modelName = modelName;
        this.restClient = RestClient.builder().baseUrl(ollamaUrl).build();
    }

    public String generateMapping(Map<String, java.util.List<String>> sourceSchema, Map<String, java.util.List<String>> destSchema) {
        String prompt = buildPrompt(sourceSchema, destSchema);

        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("model", modelName);
        requestBody.put("prompt", prompt);
        requestBody.put("stream", false);
        requestBody.put("format", "json"); // Force Ollama to only output JSON

        log.info("Sending schema mapping request to Ollama (Model: {})", modelName);

        try {
            String response = restClient.post()
                    .uri("/api/generate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody.toString())
                    .retrieve()
                    .body(String.class);

            JsonNode root = objectMapper.readTree(response);
            return root.path("response").asText();
        } catch (Exception e) {
            log.error("Failed to communicate with Ollama: {}", e.getMessage());
            throw new RuntimeException("Ollama AI mapping failed. Is Ollama running locally?", e);
        }
    }

    private String buildPrompt(Map<String, java.util.List<String>> sourceSchema, Map<String, java.util.List<String>> destSchema) {
        return """
                You are a data integration expert. Map the following source database schema to the destination database schema.
                
                Source Schema:
                %s
                
                Destination Schema:
                %s
                
                Generate a JSON object representing the mapping.
                - Keys: Destination table names.
                - Values: Objects where the key is the destination column name, and the value is the source column name (or an SQL transformation expression if needed).
                - Only output valid JSON. Do not add markdown blocks, greetings, or explanations.
                """.formatted(formatSchema(sourceSchema), formatSchema(destSchema));
    }

    private String formatSchema(Map<String, java.util.List<String>> schema) {
        StringBuilder sb = new StringBuilder();
        schema.forEach((table, columns) -> {
            sb.append("Table: ").append(table).append("\n");
            columns.forEach(col -> sb.append("  - ").append(col).append("\n"));
        });
        return sb.toString();
    }
}