package com.example.ocr.controller;

import com.example.ocr.model.OcrResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/ocr")
public class OcrController {

    @PostMapping("/process")
    public ResponseEntity<OcrResponse> processImage(
            @RequestParam("image") MultipartFile image,
            @RequestParam("modelType") String modelType,
            @RequestParam(value = "apiKey", required = false) String apiKey) {
        
        // TODO: Implement OCR processing logic
        OcrResponse response = new OcrResponse();
        response.setExtractedText("Sample extracted text");
        response.setStructuredText("Sample structured text");
        
        return ResponseEntity.ok(response);
    }
} 