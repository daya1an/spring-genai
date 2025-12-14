package com.springai.spring_genai.exception;

public class AIServiceException extends RuntimeException {
    public AIServiceException(String message) {
        super(message);
    }

    public AIServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}

