package com.example.MedicalAmountExtractor.service;


import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.MedicalAmountExtractor.model.FinalResponse;


@Service
public class AmountService {

    private final OcrService ocrService;
    private final NormalizationService normalizationService;
    private final ClassificationService classificationService;


    public AmountService(OcrService ocrService,
        NormalizationService normalizationService,
        ClassificationService classificationService) {
        this.ocrService = ocrService;
        this.normalizationService = normalizationService;
        this.classificationService = classificationService;
    }


    public FinalResponse processFile(MultipartFile file) {
        // 1. Send file to OCR microservice -> get raw text
        String rawText = ocrService.ocrFile(file);
        return processRawText(rawText);
    }


    public FinalResponse processText(String text) {
        return processRawText(text);
    }


    private FinalResponse processRawText(String rawText) {
        if (rawText == null || rawText.isBlank()) {
        FinalResponse exit = new FinalResponse();
        exit.setStatus("no_amounts_found");
        exit.setReason("document too noisy or OCR failed");
        return exit;
        }


        // 2. Extract raw numeric tokens (simple regex)
        var extractor = new RawTokenExtractor();
        var rawTokens = extractor.extractNumbersAndPercents(rawText);
        if (rawTokens.isEmpty()) {
            FinalResponse exit = new FinalResponse();
            exit.setStatus("no_amounts_found");
            exit.setReason("no numeric tokens found");
            return exit;
        }


        // 3. Normalization via LLM (Ollama)
        var norm = normalizationService.normalize(rawText, rawTokens);
        if (norm.getNormalizedAmounts().isEmpty()) {
            FinalResponse exit = new FinalResponse();
            exit.setStatus("no_amounts_found");
            exit.setReason("normalization failed");
            return exit;
        }


        // 4. Classification
        var classified = classificationService.classify(rawText, norm);


        // 5. Build final response
        FinalResponse resp = new FinalResponse();
        resp.setCurrency(classified.getCurrency());
        resp.setAmounts(classified.getAmounts());
        resp.setStatus("ok");
        return resp;
    }
}