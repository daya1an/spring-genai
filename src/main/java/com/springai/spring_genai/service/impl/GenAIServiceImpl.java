package com.springai.spring_genai.service.impl;

import com.springai.spring_genai.dto.AIRequest;
import com.springai.spring_genai.dto.AIResponse;
import com.springai.spring_genai.service.GenAIService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import com.springai.spring_genai.config.AIProperties;
import com.springai.spring_genai.entity.AIRequestEntity;
import com.springai.spring_genai.entity.RequestStatus;
import com.springai.spring_genai.exception.AIServiceException;
import com.springai.spring_genai.repository.GenAIRequestRepository;
import com.springai.spring_genai.utils.ValidationUtils;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class GenAIServiceImpl implements GenAIService {

    private final ChatClient chatClient;
    private final ValidationUtils validationUtils;
    private final GenAIRequestRepository aiRequestRepository;
    private final AIProperties appProperties;

    @Value("${spring.ai.google.genai.chat.options.model}")
    private String modelName;

    @Value("${spring.ai.google.genai.chat.options.temperature:0.7}")
    private double temperature;

    @Value("${spring.ai.google.genai.chat.options.max-output-tokens:1000}")
    private int maxTokens;

    public GenAIServiceImpl(ChatClient chatClient, ValidationUtils validationUtils, GenAIRequestRepository aiRequestRepository, AIProperties appProperties) {
        this.chatClient = chatClient;
        this.validationUtils = validationUtils;
        this.aiRequestRepository = aiRequestRepository;
        this.appProperties = appProperties;
    }

    @CircuitBreaker(name = "gemini-api", fallbackMethod = "fallbackGenerateResponse")
    @Retry(name = "gemini-retry")
    @Cacheable(value = "aiResponses", key = "#request.prompt.hashCode()", unless = "#result == null || !#result.isSuccess()")
    @Transactional
    public AIResponse generateResponse(AIRequest request) {
        String requestId = UUID.randomUUID().toString();
        long startTime = System.currentTimeMillis();

        try {
            // Validate and sanitize input
            String sanitizedPrompt = validationUtils.validateAndSanitizePrompt(request.getPrompt());
            validationUtils.validateTemperature(request.getTemperature());
            validationUtils.validateMaxTokens(request.getMaxTokens());

            log.info("Processing AI request [ID: {}]: {}", requestId, sanitizedPrompt.substring(0, Math.min(50, sanitizedPrompt.length())));

            String response = chatClient.prompt()
                    .user(sanitizedPrompt)
                    .call()
                    .content();

            long duration = System.currentTimeMillis() - startTime;

            // Save to database
            saveRequest(requestId, sanitizedPrompt, response, RequestStatus.SUCCESS, duration);

            log.info("AI Request [ID: {}] completed successfully in {}ms", requestId, duration);

            return AIResponse.builder()
                    .content(response)
                    .model(modelName)
                    .temperature(temperature)
                    .maxTokens(maxTokens)
                    .processingTimeMs(duration)
                    .success(true)
                    .requestId(requestId)
                    .build();

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("Error generating AI response [ID: {}]", requestId, e);

            // Save failed request
            saveRequest(requestId, request.getPrompt(), null, RequestStatus.FAILED, duration);

            throw new AIServiceException("Failed to generate content: " + e.getMessage(), e);
        }
    }

    @CircuitBreaker(name = "gemini-api", fallbackMethod = "fallbackSummarize")
    @Retry(name = "gemini-retry")
    @Transactional
    public AIResponse summarize(AIRequest request) {
        String requestId = UUID.randomUUID().toString();
        long startTime = System.currentTimeMillis();

        try {
            String sanitizedPrompt = validationUtils.validateAndSanitizePrompt(request.getPrompt());
            String summarizePrompt = "Please provide a concise summary of the following text:\n\n" + sanitizedPrompt;

            log.info("Summarizing content [ID: {}]", requestId);

            String response = chatClient.prompt()
                    .user(summarizePrompt)
                    .call()
                    .content();

            long duration = System.currentTimeMillis() - startTime;
            saveRequest(requestId, summarizePrompt, response, RequestStatus.SUCCESS, duration);

            return AIResponse.builder()
                    .content(response)
                    .model(modelName)
                    .processingTimeMs(duration)
                    .success(true)
                    .requestId(requestId)
                    .build();

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("Error summarizing content [ID: {}]", requestId, e);
            saveRequest(requestId, request.getPrompt(), null, RequestStatus.FAILED, duration);
            throw new AIServiceException("Failed to summarize: " + e.getMessage(), e);
        }
    }

    @CircuitBreaker(name = "gemini-api", fallbackMethod = "fallbackAnswerQuestion")
    @Retry(name = "gemini-retry")
    @Transactional
    public AIResponse answerQuestion(AIRequest request) {
        String requestId = UUID.randomUUID().toString();
        long startTime = System.currentTimeMillis();

        try {
            String sanitizedPrompt = validationUtils.validateAndSanitizePrompt(request.getPrompt());
            String questionPrompt = "Answer the following question in detail:\n\n" + sanitizedPrompt;

            log.info("Answering question [ID: {}]", requestId);

            String response = chatClient.prompt()
                    .user(questionPrompt)
                    .call()
                    .content();

            long duration = System.currentTimeMillis() - startTime;
            saveRequest(requestId, questionPrompt, response, RequestStatus.SUCCESS, duration);

            return AIResponse.builder()
                    .content(response)
                    .model(modelName)
                    .processingTimeMs(duration)
                    .success(true)
                    .requestId(requestId)
                    .build();

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("Error answering question [ID: {}]", requestId, e);
            saveRequest(requestId, request.getPrompt(), null, RequestStatus.FAILED, duration);
            throw new AIServiceException("Failed to answer question: " + e.getMessage(), e);
        }
    }

    @CircuitBreaker(name = "gemini-api", fallbackMethod = "fallbackTranslate")
    @Retry(name = "gemini-retry")
    @Transactional
    public AIResponse translateText(AIRequest request) {
        String requestId = UUID.randomUUID().toString();
        long startTime = System.currentTimeMillis();

        try {
            validationUtils.validateTargetLanguage(request.getTargetLanguage());
            String sanitizedPrompt = validationUtils.validateAndSanitizePrompt(request.getPrompt());
            String translatePrompt = "Translate the following text to " + request.getTargetLanguage() +
                    ":\n\n" + sanitizedPrompt;

            log.info("Translating text [ID: {}] to {}", requestId, request.getTargetLanguage());

            String response = chatClient.prompt()
                    .user(translatePrompt)
                    .call()
                    .content();

            long duration = System.currentTimeMillis() - startTime;
            saveRequest(requestId, translatePrompt, response, RequestStatus.SUCCESS, duration);

            return AIResponse.builder()
                    .content(response)
                    .model(modelName)
                    .processingTimeMs(duration)
                    .success(true)
                    .requestId(requestId)
                    .build();

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("Error translating text [ID: {}]", requestId, e);
            saveRequest(requestId, request.getPrompt(), null, RequestStatus.FAILED, duration);
            throw new AIServiceException("Failed to translate: " + e.getMessage(), e);
        }
    }

    @CircuitBreaker(name = "gemini-api", fallbackMethod = "fallbackCodeGeneration")
    @Retry(name = "gemini-retry")
    @Transactional
    public AIResponse codeGeneration(AIRequest request) {
        String requestId = UUID.randomUUID().toString();
        long startTime = System.currentTimeMillis();

        try {
            String sanitizedPrompt = validationUtils.validateAndSanitizePrompt(request.getPrompt());
            String codePrompt = "Generate clean, well-documented code for the following requirement:\n\n" + sanitizedPrompt;

            log.info("Generating code [ID: {}]", requestId);

            String response = chatClient.prompt()
                    .user(codePrompt)
                    .call()
                    .content();

            long duration = System.currentTimeMillis() - startTime;
            saveRequest(requestId, codePrompt, response, RequestStatus.SUCCESS, duration);

            return AIResponse.builder()
                    .content(response)
                    .model(modelName)
                    .processingTimeMs(duration)
                    .success(true)
                    .requestId(requestId)
                    .build();

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("Error generating code [ID: {}]", requestId, e);
            saveRequest(requestId, request.getPrompt(), null, RequestStatus.FAILED, duration);
            throw new AIServiceException("Failed to generate code: " + e.getMessage(), e);
        }
    }

    @CircuitBreaker(name = "gemini-api", fallbackMethod = "fallbackAnalyzeText")
    @Retry(name = "gemini-retry")
    @Transactional
    public AIResponse analyzeText(AIRequest request) {
        String requestId = UUID.randomUUID().toString();
        long startTime = System.currentTimeMillis();

        try {
            String sanitizedPrompt = validationUtils.validateAndSanitizePrompt(request.getPrompt());
            String analyzePrompt = "Provide a detailed analysis of the following text, including key themes, sentiment, and insights:\n\n" + sanitizedPrompt;

            log.info("Analyzing text [ID: {}]", requestId);

            String response = chatClient.prompt()
                    .user(analyzePrompt)
                    .call()
                    .content();

            long duration = System.currentTimeMillis() - startTime;
            saveRequest(requestId, analyzePrompt, response, RequestStatus.SUCCESS, duration);

            return AIResponse.builder()
                    .content(response)
                    .model(modelName)
                    .processingTimeMs(duration)
                    .success(true)
                    .requestId(requestId)
                    .build();

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("Error analyzing text [ID: {}]", requestId, e);
            saveRequest(requestId, request.getPrompt(), null, RequestStatus.FAILED, duration);
            throw new AIServiceException("Failed to analyze text: " + e.getMessage(), e);
        }
    }

    @Override
    public AIResponse chatWithContext(AIRequest request) {
        return null;
    }

    // ============================================
    // FALLBACK METHODS (Circuit Breaker)
    // ============================================
    public AIResponse fallbackGenerateResponse(AIRequest request, Exception e) {
        log.warn("Circuit breaker activated for generateResponse: {}", e.getMessage());
        return AIResponse.builder()
                .success(false)
                .errorMessage("Service temporarily unavailable. Please try again later.")
                .build();
    }

    public AIResponse fallbackSummarize(AIRequest request, Exception e) {
        log.warn("Circuit breaker activated for summarize: {}", e.getMessage());
        return AIResponse.builder()
                .success(false)
                .errorMessage("Summarize service temporarily unavailable.")
                .build();
    }

    public AIResponse fallbackAnswerQuestion(AIRequest request, Exception e) {
        log.warn("Circuit breaker activated for answerQuestion: {}", e.getMessage());
        return AIResponse.builder()
                .success(false)
                .errorMessage("Question answering service temporarily unavailable.")
                .build();
    }

    public AIResponse fallbackTranslate(AIRequest request, Exception e) {
        log.warn("Circuit breaker activated for translate: {}", e.getMessage());
        return AIResponse.builder()
                .success(false)
                .errorMessage("Translation service temporarily unavailable.")
                .build();
    }

    public AIResponse fallbackCodeGeneration(AIRequest request, Exception e) {
        log.warn("Circuit breaker activated for codeGeneration: {}", e.getMessage());
        return AIResponse.builder()
                .success(false)
                .errorMessage("Code generation service temporarily unavailable.")
                .build();
    }

    public AIResponse fallbackAnalyzeText(AIRequest request, Exception e) {
        log.warn("Circuit breaker activated for analyzeText: {}", e.getMessage());
        return AIResponse.builder()
                .success(false)
                .errorMessage("Text analysis service temporarily unavailable.")
                .build();
    }

    // ============================================
    // HELPER METHODS
    // ============================================
    @Transactional
    private void saveRequest(String requestId, String prompt, String response, RequestStatus status, long duration) {
        try {
            String userId = getCurrentUserId();

            AIRequestEntity entity = AIRequestEntity.builder()
                    .prompt(prompt)
                    .response(response)
                    .status(status)
                    .userId(userId)
                    .model(modelName)
                    .processingTimeMs(duration)
                    .temperature(temperature)
                    .maxTokens(maxTokens)
                    .build();

            aiRequestRepository.save(entity);
            log.debug("Request [ID: {}] saved to database", requestId);

        } catch (Exception e) {
            log.error("Failed to save request to database: {}", e.getMessage());
        }
    }

    private String getCurrentUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                return authentication.getName();
            }
        } catch (Exception e) {
            log.debug("Unable to retrieve current user", e);
        }
        return "UNKNOWN";
    }
}