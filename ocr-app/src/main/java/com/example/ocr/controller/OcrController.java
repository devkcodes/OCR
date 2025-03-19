package com.example.ocr.controller;

import com.example.ocr.model.ErrorResponse;
import com.example.ocr.model.OcrResponse;
import com.example.ocr.service.OcrService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/ocr")
public class OcrController {
    private static final Logger logger = LoggerFactory.getLogger(OcrController.class);
    private final OcrService ocrService;

    public OcrController(OcrService ocrService) {
        this.ocrService = ocrService;
    }

    @PostMapping("/process")
    public ResponseEntity<?> processImage(
            @RequestParam("image") MultipartFile image,
            @RequestParam("modelType") String modelType,
            @RequestParam(value = "apiKey", required = false) String apiKey) {
        try {
            if (image.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Please provide an image file"));
            }

            String contentType = image.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("File must be an image"));
            }

            OcrResponse response = ocrService.processImage(image, modelType, apiKey);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error processing image", e);
            return ResponseEntity.internalServerError()
                .body(new ErrorResponse("Failed to process image: " + e.getMessage()));
        }
    }
} 