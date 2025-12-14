package com.springai.spring_genai.service;

import com.springai.spring_genai.dto.AIRequest;
import com.springai.spring_genai.dto.AIResponse;

public interface GenAIService {

    public AIResponse generateResponse(AIRequest request);

    public AIResponse summarize(AIRequest request);

    public AIResponse answerQuestion(AIRequest request);

    public AIResponse translateText(AIRequest request);

    public AIResponse codeGeneration(AIRequest request);

    public AIResponse analyzeText(AIRequest request);

    public AIResponse chatWithContext(AIRequest request);
}
