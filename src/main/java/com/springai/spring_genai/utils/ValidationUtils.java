package com.springai.spring_genai.utils;

import com.springai.spring_genai.config.AIProperties;
import com.springai.spring_genai.exception.InvalidPromptException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
@Slf4j
public class ValidationUtils {

    private final AIProperties AIProperties;
    private static final Pattern MALICIOUS_PATTERN = Pattern.compile("[<>\"'%;()&+\\\\]");
    private static final int MAX_PROMPT_LENGTH = 10000;
    private static final int MIN_PROMPT_LENGTH = 1;

    public ValidationUtils(AIProperties AIProperties) {
        this.AIProperties = AIProperties;
    }

    public String validateAndSanitizePrompt(String prompt) throws InvalidPromptException {
        if (prompt == null || prompt.isBlank()) {
            throw new InvalidPromptException("Prompt cannot be blank");
        }

        String trimmedPrompt = prompt.trim();

        if (trimmedPrompt.length() < MIN_PROMPT_LENGTH) {
            throw new InvalidPromptException(
                    String.format("Prompt must be at least %d character long", MIN_PROMPT_LENGTH)
            );
        }

        if (trimmedPrompt.length() > MAX_PROMPT_LENGTH) {
            throw new InvalidPromptException(
                    String.format("Prompt exceeds maximum length of %d characters", MAX_PROMPT_LENGTH)
            );
        }
//          enable if needed
//        if (containsSuspiciousPatterns(trimmedPrompt)) {
//            log.warn("Suspicious patterns detected in prompt");
//            throw new InvalidPromptException("Prompt contains suspicious patterns");
//        }

        if (AIProperties.getValidation().isEnableSanitization()) {
            return sanitizePrompt(trimmedPrompt);
        }

        return trimmedPrompt;
    }

    private boolean containsSuspiciousPatterns(String prompt) {
        return MALICIOUS_PATTERN.matcher(prompt).find();
    }

    private String sanitizePrompt(String prompt) {
        return prompt
                .replaceAll("[<>]", "")
                .replaceAll("['\\\"]", "'")
                .replaceAll("[%;()&+]", "");
    }

    public void validateTargetLanguage(String language) throws InvalidPromptException {
        if (language == null || language.isBlank()) {
            throw new InvalidPromptException("Target language cannot be blank");
        }

        if (language.length() > 50) {
            throw new InvalidPromptException("Target language is invalid");
        }

        // Allow alphanumeric and spaces only
        if (!language.matches("^[a-zA-Z\\s-]+$")) {
            throw new InvalidPromptException("Target language contains invalid characters");
        }
    }

    public void validateTemperature(Double temperature) throws InvalidPromptException {
        if (temperature != null && (temperature < 0.0 || temperature > 2.0)) {
            throw new InvalidPromptException("Temperature must be between 0.0 and 2.0");
        }
    }

    public void validateMaxTokens(Integer maxTokens) throws InvalidPromptException {
        if (maxTokens != null && (maxTokens < 1 || maxTokens > 100000)) {
            throw new InvalidPromptException("Max tokens must be between 1 and 100000");
        }
    }

    public String escapeHtml(String input) {
        if (input == null) {
            return null;
        }
        return input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    public boolean isValidIpAddress(String ip) {
        return ip != null && ip.matches("^(\\d{1,3}\\.){3}\\d{1,3}$");
    }
}
