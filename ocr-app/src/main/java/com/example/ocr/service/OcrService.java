package com.example.ocr.service;

import com.example.ocr.model.OcrResponse;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

import jakarta.annotation.PostConstruct;

@Service
public class OcrService {
    private static final Logger logger = LoggerFactory.getLogger(OcrService.class);

    private final RestTemplate restTemplate;
    private final Tesseract tesseract;

    @Value("${ollama.api.url}")
    private String ollamaApiUrl;

    @Value("${ollama.model.name}")
    private String ollamaModelName;

    @Value("${tesseract.data.path:/usr/share/tessdata}")
    private String tesseractDataPath;

    public OcrService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.tesseract = new Tesseract();
    }

    @PostConstruct
    public void init() {
        try {
            // Initialize Tesseract with explicit data path
            tesseract.setDatapath(tesseractDataPath);
            // Set other Tesseract parameters
            tesseract.setLanguage("eng"); // English language
            tesseract.setPageSegMode(1); // Automatic page segmentation with OSD
            tesseract.setOcrEngineMode(1); // Neural net LSTM engine
        } catch (Exception e) {
            logger.error("Failed to initialize Tesseract", e);
            throw new RuntimeException("Failed to initialize OCR engine", e);
        }
    }

    public OcrResponse processImage(MultipartFile image, String modelType, String apiKey) throws IOException, TesseractException {
        // Create a temporary file to store the image
        Path tempFile = null;
        try {
            // Create temp directory if it doesn't exist
            Path tempDir = Files.createTempDirectory("ocr-temp");
            tempFile = tempDir.resolve(UUID.randomUUID() + "-" + image.getOriginalFilename());
            image.transferTo(tempFile.toFile());

            // Process image with Tesseract
            BufferedImage bufferedImage = ImageIO.read(tempFile.toFile());
            if (bufferedImage == null) {
                throw new IOException("Failed to read image file");
            }

            String extractedText = tesseract.doOCR(bufferedImage);
            logger.debug("Extracted text: {}", extractedText);

            // Process with AI model
            String structuredText = switch (modelType.toLowerCase()) {
                case "ollama" -> processWithOllama(extractedText);
                case "openai" -> processWithOpenAI(extractedText, apiKey);
                default -> throw new IllegalArgumentException("Unsupported model type: " + modelType);
            };

            return new OcrResponse(extractedText, structuredText);

        } catch (TesseractException e) {
            logger.error("Tesseract OCR error", e);
            throw new RuntimeException("OCR processing failed: " + e.getMessage());
        } catch (IOException e) {
            logger.error("File processing error", e);
            throw new IOException("Failed to process image file: " + e.getMessage());
        } finally {
            // Cleanup temporary files
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                    Files.deleteIfExists(tempFile.getParent());
                } catch (IOException e) {
                    logger.warn("Failed to cleanup temporary files", e);
                }
            }
        }
    }

    private String processWithOllama(String extractedText) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String prompt = """
                Analyze and structure the following extracted text from an image.
                Format it properly, correct any obvious OCR errors, and organize it clearly:
                
                %s
                """.formatted(extractedText);

            Map<String, Object> requestBody = Map.of(
                "model", ollamaModelName,
                "prompt", prompt,
                "stream", false
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            var response = restTemplate.postForObject(ollamaApiUrl, request, Map.class);

            if (response == null || !response.containsKey("response")) {
                throw new RuntimeException("Invalid response from Ollama API");
            }

            return (String) response.get("response");
        } catch (Exception e) {
            logger.error("Ollama processing error", e);
            throw new RuntimeException("Failed to process with Ollama: " + e.getMessage());
        }
    }

    private String processWithOpenAI(String extractedText, String apiKey) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> message = Map.of(
                "role", "user",
                "content", "Analyze and structure this extracted text: " + extractedText
            );

            Map<String, Object> requestBody = Map.of(
                "model", "gpt-4o-mini",
                "messages", new Object[]{message},
                "temperature", 0.7,
                "max_tokens", 1000
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            var response = restTemplate.postForObject(
                "https://api.openai.com/v1/chat/completions",
                request,
                Map.class
            );

            if (response != null && response.containsKey("choices")) {
                var choices = (java.util.List<?>) response.get("choices");
                if (!choices.isEmpty()) {
                    var choice = (Map<?, ?>) choices.get(0);
                    var messageResponse = (Map<?, ?>) choice.get("message");
                    return (String) messageResponse.get("content");
                }
            }

            throw new RuntimeException("Invalid response from OpenAI API");
        } catch (Exception e) {
            logger.error("OpenAI processing error", e);
            throw new RuntimeException("Failed to process with OpenAI: " + e.getMessage());
        }
    }
} 