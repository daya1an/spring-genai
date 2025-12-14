package com.springai.spring_genai.controller;

import com.springai.spring_genai.dto.AIRequest;
import com.springai.spring_genai.dto.AIResponse;
import com.springai.spring_genai.dto.ModelInfo;
import com.springai.spring_genai.service.GenAIService;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/ai")
@Validated
@Slf4j
public class GenAIController {

    private final GenAIService genAIService;

    public GenAIController(GenAIService genAIService) {
        this.genAIService = genAIService;
    }

    @PostMapping("/generate")
    public ResponseEntity<AIResponse> generateResponse(@Valid @RequestBody AIRequest request) {
        log.info("Received generate request");
        AIResponse response = genAIService.generateResponse(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/summarize")
    public ResponseEntity<AIResponse> summarize(@Valid @RequestBody AIRequest request) {
        log.info("Received summarize request");
        AIResponse response = genAIService.summarize(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/question")
    public ResponseEntity<AIResponse> answerQuestion(@Valid @RequestBody AIRequest request) {
        log.info("Received question request");
        AIResponse response = genAIService.answerQuestion(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/translate")
    public ResponseEntity<AIResponse> translateText(@Valid @RequestBody AIRequest request) {
        log.info("Received translate request");
        if (request.getTargetLanguage() == null || request.getTargetLanguage().isBlank()) {
            return ResponseEntity.badRequest().body(
                    AIResponse.builder()
                            .success(false)
                            .errorMessage("Target language is required for translation")
                            .build()
            );
        }
        AIResponse response = genAIService.translateText(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/generate-code")
    public ResponseEntity<AIResponse> generateCode(@Valid @RequestBody AIRequest request) {
        log.info("Received code generation request");
        AIResponse response = genAIService.codeGeneration(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/analyze")
    public ResponseEntity<AIResponse> analyzeText(@Valid @RequestBody AIRequest request) {
        log.info("Received text analysis request");
        AIResponse response = genAIService.analyzeText(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/chat")
    public ResponseEntity<AIResponse> chat(@Valid @RequestBody AIRequest request) {
        log.info("Received chat request");
        AIResponse response = genAIService.chatWithContext(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Google Gemini AI Service is running");
    }

    @Value("${spring.ai.google.genai.chat.options.model}")
    private String modelName;

    @GetMapping("/model-info")
    public ResponseEntity<ModelInfo> getModelInfo() {
        return ResponseEntity.ok(
                ModelInfo.builder()
                        .provider("Google Gemini")
                        .model(modelName)
                        .version("2.5")
                        .status("ACTIVE")
                        .description("Dynamically configured Google Gemini model")
                        .build()
        );
    }
}
