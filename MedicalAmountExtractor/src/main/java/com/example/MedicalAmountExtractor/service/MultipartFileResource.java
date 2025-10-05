package com.example.MedicalAmountExtractor.service;

import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;


import java.io.ByteArrayInputStream;
import java.io.IOException;


// Helper to send MultipartFile via RestTemplate
public class MultipartFileResource extends InputStreamResource {
private final String filename;


public MultipartFileResource(MultipartFile multipartFile) throws IOException {
super(new ByteArrayInputStream(multipartFile.getBytes()));
this.filename = multipartFile.getOriginalFilename();
}


@Override
public String getFilename() {
return this.filename;
}
}
