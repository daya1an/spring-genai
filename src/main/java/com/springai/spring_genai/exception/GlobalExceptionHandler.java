package com.springai.spring_genai.exception;
import com.springai.spring_genai.exception.ErrorResponse;
import com.springai.spring_genai.exception.ValidationErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidPromptException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPromptException(
            InvalidPromptException ex, HttpServletRequest request) {

        log.warn("Invalid prompt: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode("INVALID_PROMPT")
                .errorMessage(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .status(HttpStatus.BAD_REQUEST.value())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleRateLimitException(
            RateLimitExceededException ex, HttpServletRequest request) {

        log.warn("Rate limit exceeded: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode("RATE_LIMIT_EXCEEDED")
                .errorMessage(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .status(HttpStatus.TOO_MANY_REQUESTS.value())
                .build();

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(errorResponse);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(
            UnauthorizedException ex, HttpServletRequest request) {

        log.warn("Unauthorized access: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode("UNAUTHORIZED")
                .errorMessage(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .status(HttpStatus.UNAUTHORIZED.value())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(AIServiceException.class)
    public ResponseEntity<ErrorResponse> handleAIServiceException(
            AIServiceException ex, HttpServletRequest request) {

        log.error("AI Service Error", ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode("AI_SERVICE_ERROR")
                .errorMessage(ex.getMessage())
                .errorDetails(ex.getCause() != null ? ex.getCause().getMessage() : null)
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        log.warn("Validation failed for request: {}", request.getRequestURI());

        List<ValidationErrorResponse.FieldError> fieldErrors = new ArrayList<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.add(ValidationErrorResponse.FieldError.builder()
                        .field(error.getField())
                        .message(error.getDefaultMessage())
                        .rejectedValue(error.getRejectedValue())
                        .build())
        );

        ValidationErrorResponse errorResponse = ValidationErrorResponse.builder()
                .timestamp(LocalDateTime.now().toString())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Failed")
                .message("Input validation failed")
                .path(request.getRequestURI())
                .errors(fieldErrors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(
            NoHandlerFoundException ex, HttpServletRequest request) {

        log.warn("Endpoint not found: {}", request.getRequestURI());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode("NOT_FOUND")
                .errorMessage("Requested endpoint not found")
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .status(HttpStatus.NOT_FOUND.value())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {

        log.error("Unexpected error occurred", ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode("INTERNAL_SERVER_ERROR")
                .errorMessage("An unexpected error occurred")
                .errorDetails(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
