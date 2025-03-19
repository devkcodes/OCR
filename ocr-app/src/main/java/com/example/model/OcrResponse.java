package com.example.ocr.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OcrResponse {
    // Raw text extracted from the image
    private String extractedText;
    
    // Structured text processed by Gemma 3
    private String structuredText;
} 