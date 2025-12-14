package com.springai.spring_genai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AIResponse {

    private String content;

    private String model;

    private Double temperature;

    private Integer maxTokens;

    private boolean success;

    private Long processingTimeMs;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    private String errorMessage;

    private String requestId;
}
