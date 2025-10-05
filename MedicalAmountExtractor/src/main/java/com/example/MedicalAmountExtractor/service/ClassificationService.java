package com.example.MedicalAmountExtractor.service;

import com.example.MedicalAmountExtractor.model.AmountEntry;
import com.example.MedicalAmountExtractor.model.NormalizationResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.util.ArrayList;
import java.util.List;

@Service
public class ClassificationService {
    @Value("${ollama.base.url}")
    private String ollamaBaseUrl;


    @Value("${ollama.model}")
    private String ollamaModel;


    private final RestTemplate rest = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper(); 
    
    public ClassifiedResult classify(String rawText, NormalizationResult norm) {
        try {
            String prompt = "Given the text and corrected numeric amounts, label each amount with its context (types: total_bill, paid, due, discount, tax, other). " +
            "Return JSON: { \"currency\": \"INR\", \"amounts\": [{\"type\":..., \"value\":..., \"source\":...}], \"confidence\": 0.8 }\n\n" +
            "Text:\n" + rawText + "\n\n" +
            "Normalized amounts: " + norm.getNormalizedAmounts().toString() + "\n\n";


            String url = ollamaBaseUrl + "/api/generate";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            var payload = mapper.createObjectNode();
            payload.put("model", ollamaModel);
            payload.put("prompt", prompt);
            payload.put("max_tokens", 512);
            HttpEntity<String> entity = new HttpEntity<>(mapper.writeValueAsString(payload), headers);
            String resp = rest.postForObject(url, entity, String.class);
            if (resp == null) return fallbackClassification(norm);


            int firstBrace = resp.indexOf('{');
            if (firstBrace == -1) return fallbackClassification(norm);
            String jsonPart = resp.substring(firstBrace);
            JsonNode node = mapper.readTree(jsonPart);


            ClassifiedResult result = new ClassifiedResult();
            if (node.has("currency")) result.setCurrency(node.get("currency").asText());
            if (node.has("confidence")) result.setConfidence(node.get("confidence").asDouble());
            if (node.has("amounts")) {
            for (JsonNode a : node.get("amounts")) {
            AmountEntry e = new AmountEntry();
            e.setType(a.has("type") ? a.get("type").asText() : "other");
            e.setValue(a.has("value") ? a.get("value").asDouble() : 0);
            e.setSource(a.has("source") ? a.get("source").asText() : "text");
            result.getAmounts().add(e);
            }
            }
            // If classification failed to produce amounts, fallback
            if (result.getAmounts().isEmpty()) return fallbackClassification(norm);
            return result;


        } catch (Exception ex) {
            ex.printStackTrace();
            return fallbackClassification(norm);
        }
    }    

    private ClassifiedResult fallbackClassification(NormalizationResult norm) {
        ClassifiedResult r = new ClassifiedResult();
        r.setCurrency(norm.getCurrencyHint() == null ? "INR" : norm.getCurrencyHint());
        r.setConfidence(norm.getNormalizationConfidence());
        List<AmountEntry> list = new ArrayList<>();
        var vals = norm.getNormalizedAmounts();
        String[] types = {"total_bill","paid","due","other"};
        for (int i = 0; i < vals.size(); i++) {
        AmountEntry e = new AmountEntry();
        e.setType(i < types.length ? types[i] : "other");
        e.setValue(vals.get(i));
        e.setSource("derived");
        list.add(e);
        }
        r.setAmounts(list);
        return r;
    }

    public static class ClassifiedResult {
        private String currency;
        private List<AmountEntry> amounts = new ArrayList<>();
        private double confidence = 0.0;


        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
        public List<AmountEntry> getAmounts() { return amounts; }
        public void setAmounts(List<AmountEntry> amounts) { this.amounts = amounts; }
        public double getConfidence() { return confidence; }
        public void setConfidence(double confidence) { this.confidence = confidence; }
    }
}
