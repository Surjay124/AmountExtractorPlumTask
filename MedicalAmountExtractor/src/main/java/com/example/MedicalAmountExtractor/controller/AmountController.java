package com.example.MedicalAmountExtractor.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.MedicalAmountExtractor.model.FinalResponse;
import com.example.MedicalAmountExtractor.service.AmountService;

import jakarta.validation.constraints.NotBlank;


@RestController
@RequestMapping("/api/amount")
public class AmountController {


private final AmountService amountService;


public AmountController(AmountService amountService) {
this.amountService = amountService;
}


@PostMapping(value = "/extract/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<FinalResponse> extractFromFile(@RequestParam("file") MultipartFile file) {
FinalResponse resp = amountService.processFile(file);
return ResponseEntity.ok(resp);
}


@PostMapping(value = "/extract/text", consumes = MediaType.APPLICATION_JSON_VALUE)
public ResponseEntity<FinalResponse> extractFromText(@RequestBody @NotBlank String text) {
FinalResponse resp = amountService.processText(text);
return ResponseEntity.ok(resp);
}
}

