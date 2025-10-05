package com.example.MedicalAmountExtractor.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;


// import java.nio.charset.StandardCharsets;

@Service
public class OcrService {
    @Value("${ocr.service.url}")
    private String ocrServiceUrl;


    private final RestTemplate rest = new RestTemplate(); 
    
    // Expecting OCR microservice that accepts multipart file and returns { "text": "..." }
    public String ocrFile(MultipartFile file){
        try {
           HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);


            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new MultipartFileResource(file));


            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = rest.postForEntity(ocrServiceUrl, requestEntity, String.class);
            // naive parsing: microservice returns raw text as plain string or JSON {"text":...}
                String resp = response.getBody();
                if (resp == null) return null;
                // if JSON wrapper, try to extract text field
                if (resp.trim().startsWith("{")) {
                // simple extract (not robust): look for "text":"..."
                var idx = resp.indexOf("\"text\"");
                if (idx != -1) {
                    var start = resp.indexOf(':', idx) + 1;
                    var s = resp.substring(start).trim();
                    s = s.replaceAll("^[\\\\s:\\\\\"]+", "");
                    s = s.replaceAll("[\\\\\"}]+$", "");
                    return s;
                    }
                }
            return resp; 
        } catch (Exception ex) {
           ex.printStackTrace();
          return null;
        }
    }

}