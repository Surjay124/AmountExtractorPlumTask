package com.example.MedicalAmountExtractor.service;

import com.example.MedicalAmountExtractor.model.NormalizationResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
// import java.util.stream.Collectors;

@Service
public class NormalizationService {
    @Value("${ollama.base.url}")
    private String ollamaBaseUrl;


    @Value("${ollama.model}")
    private String ollamaModel;


    private final RestTemplate rest = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    public NormalizationResult normalize(String rawText, List<String> rawTokens) {
        try {
            // Build prompt instructing the model to correct OCR errors and return JSON
            String prompt = "Given the OCR-extracted text below, find numeric tokens and correct OCR digit/character errors. " +
            "Return JSON with keys: normalized_amounts (array of numbers) and currency_hint (e.g., INR) and normalization_confidence (0-1).\n\n" +
            "Text:\n" + rawText + "\n\n" +
            "Raw tokens: " + rawTokens.toString() + "\n\n" +
            "Example output:\n{\n \"normalized_amounts\": [1200,1000,200],\n \"currency_hint\": \"INR\",\n \"normalization_confidence\": 0.82\n}\n";


            // Ollama HTTP inference: POST /api/generate with JSON { model:..., prompt: ... }
            String url = ollamaBaseUrl + "/api/generate";


            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);


            var payload = mapper.createObjectNode();
            payload.put("model", ollamaModel);
            payload.put("prompt", prompt);
            payload.put("max_tokens", 512);


            HttpEntity<String> entity = new HttpEntity<>(mapper.writeValueAsString(payload), headers);
            String resp = rest.postForObject(url, entity, String.class);
            if (resp == null) return new NormalizationResult();


            // Try to extract JSON object from response text
            int firstBrace = resp.indexOf('{');
            if (firstBrace == -1) return new NormalizationResult();
            String jsonPart = resp.substring(firstBrace);
            JsonNode node = mapper.readTree(jsonPart);


            NormalizationResult result = new NormalizationResult();
            if (node.has("normalized_amounts")) {
                var arr = node.get("normalized_amounts");
                for (JsonNode n : arr) {
                    result.getNormalizedAmounts().add(n.asDouble() % 1 == 0 ? n.intValue() : n.doubleValue());
                }
            }
            if (node.has("currency_hint")) result.setCurrencyHint(node.get("currency_hint").asText());
            if (node.has("normalization_confidence")) result.setNormalizationConfidence(node.get("normalization_confidence").asDouble());
            return result;


        } catch (Exception ex) {
                ex.printStackTrace();
                return new NormalizationResult();
        }
    }
}