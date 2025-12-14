package com.springai.spring_genai.exception;

public class InvalidPromptException extends RuntimeException {
    public InvalidPromptException(String message) {
        super(message);
    }

    public InvalidPromptException(String message, Throwable cause) {
        super(message, cause);
    }
}
