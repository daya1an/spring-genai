package com.springai.spring_genai.service.impl;

import com.springai.spring_genai.dto.AIRequest;
import com.springai.spring_genai.dto.AIResponse;
import com.springai.spring_genai.service.GenAIService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;


@Service
@Slf4j
public class GenAIServiceImpl implements GenAIService {

//    @Value("${spring.ai.google.generativeai.chat.options.model}")
//    private String modelName;
//
//    @Value("${spring.ai.google.generativeai.chat.options.temperature:0.7}")
//    private double temperature;
//
//    @Value("${spring.ai.google.generativeai.chat.options.max-output-tokens:1000}")
//    private int maxTokens;
//
//    private final ChatClient chatClient;
//
//    public GenAIServiceImpl(String modelName, double temperature, int maxTokens, ChatClient chatClient) {
//        this.modelName = modelName;
//        this.temperature = temperature;
//        this.maxTokens = maxTokens;
//        this.chatClient = chatClient;
//    }
private final String modelName;
    private final double temperature;
    private final int maxTokens;
    private final ChatClient chatClient;

    public GenAIServiceImpl(
            @Value("${spring.ai.google.genai.chat.options.model}") String modelName,
            @Value("${spring.ai.google.genai.chat.options.temperature}") double temperature,
            @Value("${spring.ai.google.genai.chat.options.max-output-tokens}") int maxTokens,
            ChatClient chatClient) {
        this.modelName = modelName;
        this.temperature = temperature;
        this.maxTokens = maxTokens;
        this.chatClient = chatClient;
    }

    public AIResponse generateResponse(AIRequest request) {
        try {
            log.info("Processing AI request with Gemini: {}", request.getPrompt());

            String response = chatClient.prompt()
                    .user(request.getPrompt())
                    .call()
                    .content();

            log.info("Gemini response generated successfully");

            return AIResponse.builder()
                    .content(response)
                    .model(modelName)
//                    .temperature(temperature)
//                    .maxTokens(maxTokens)
//                    .success(true)
                    .build();

        } catch (Exception e) {
            log.error("Error generating AI response: {}", e.getMessage(), e);
            return AIResponse.builder()
                    .content("Error: " + e.getMessage())
                    .model(modelName)
                    .success(false)
                    .errorMessage(e.getMessage())
                    .build();
        }
    }

    public AIResponse summarize(AIRequest request) {
        try {
            log.info("Summarizing content with Gemini");

            String summarizePrompt = "Please provide a concise summary of the following text:\n\n" + request.getPrompt();

            String response = chatClient.prompt()
                    .user(summarizePrompt)
                    .call()
                    .content();

            return AIResponse.builder()
                    .content(response)
                    .model(modelName)
                    .temperature(temperature)
                    .maxTokens(maxTokens)
                    .success(true)
                    .build();

        } catch (Exception e) {
            log.error("Error summarizing content: {}", e.getMessage(), e);
            return AIResponse.builder()
                    .content("Error: " + e.getMessage())
                    .model(modelName)
                    .success(false)
                    .errorMessage(e.getMessage())
                    .build();
        }
    }

    public AIResponse answerQuestion(AIRequest request) {
        try {
            log.info("Answering question with Gemini: {}", request.getPrompt());

            String questionPrompt = "Answer the following question in detail:\n\n" + request.getPrompt();

            String response = chatClient.prompt()
                    .user(questionPrompt)
                    .call()
                    .content();

            return AIResponse.builder()
                    .content(response)
                    .model(modelName)
                    .temperature(temperature)
                    .maxTokens(maxTokens)
                    .success(true)
                    .build();

        } catch (Exception e) {
            log.error("Error answering question: {}", e.getMessage(), e);
            return AIResponse.builder()
                    .content("Error: " + e.getMessage())
                    .model(modelName)
                    .success(false)
                    .errorMessage(e.getMessage())
                    .build();
        }
    }

    public AIResponse translateText(AIRequest request) {
        try {
            log.info("Translating text to: {} with Gemini", request.getTargetLanguage());

            String translatePrompt = "Translate the following text to " + request.getTargetLanguage() +
                    ":\n\n" + request.getPrompt();

            String response = chatClient.prompt()
                    .user(translatePrompt)
                    .call()
                    .content();

            return AIResponse.builder()
                    .content(response)
                    .model(modelName)
                    .temperature(temperature)
                    .maxTokens(maxTokens)
                    .success(true)
                    .build();

        } catch (Exception e) {
            log.error("Error translating text: {}", e.getMessage(), e);
            return AIResponse.builder()
                    .content("Error: " + e.getMessage())
                    .model(modelName)
                    .success(false)
                    .errorMessage(e.getMessage())
                    .build();
        }
    }

    public AIResponse codeGeneration(AIRequest request) {
        try {
            log.info("Generating code with Gemini");

            String codePrompt = "Generate clean, well-documented code for the following requirement:\n\n" + request.getPrompt();

            String response = chatClient.prompt()
                    .user(codePrompt)
                    .call()
                    .content();

            return AIResponse.builder()
                    .content(response)
                    .model(modelName)
                    .temperature(temperature)
                    .maxTokens(maxTokens)
                    .success(true)
                    .build();

        } catch (Exception e) {
            log.error("Error generating code: {}", e.getMessage(), e);
            return AIResponse.builder()
                    .content("Error: " + e.getMessage())
                    .model(modelName)
                    .success(false)
                    .errorMessage(e.getMessage())
                    .build();
        }
    }

    public AIResponse analyzeText(AIRequest request) {
        try {
            log.info("Analyzing text with Gemini");

            String analyzePrompt = "Provide a detailed analysis of the following text, including key themes, sentiment, and insights:\n\n" + request.getPrompt();

            String response = chatClient.prompt()
                    .user(analyzePrompt)
                    .call()
                    .content();

            return AIResponse.builder()
                    .content(response)
                    .model(modelName)
                    .temperature(temperature)
                    .maxTokens(maxTokens)
                    .success(true)
                    .build();

        } catch (Exception e) {
            log.error("Error analyzing text: {}", e.getMessage(), e);
            return AIResponse.builder()
                    .content("Error: " + e.getMessage())
                    .model(modelName)
                    .success(false)
                    .errorMessage(e.getMessage())
                    .build();
        }
    }

    public AIResponse chatWithContext(AIRequest request) {
        try {
            log.info("Chat with context: {}", request.getPrompt());

            String response = chatClient.prompt()
                    .user(request.getPrompt())
                    .call()
                    .content();

            return AIResponse.builder()
                    .content(response)
                    .model(modelName)
                    .temperature(temperature)
                    .maxTokens(maxTokens)
                    .success(true)
                    .build();

        } catch (Exception e) {
            log.error("Error in chat: {}", e.getMessage(), e);
            return AIResponse.builder()
                    .content("Error: " + e.getMessage())
                    .model(modelName)
                    .success(false)
                    .errorMessage(e.getMessage())
                    .build();
        }
    }
}