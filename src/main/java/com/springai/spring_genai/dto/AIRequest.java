package com.springai.spring_genai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AIRequest {

    @NotBlank(message = "Prompt cannot be blank")
    @Size(min = 1, max = 10000, message = "Prompt must be between 1 and 10000 characters")
    private String prompt;

    @Size(max = 100, message = "Target language must not exceed 100 characters")
    private String targetLanguage;

    @Min(value = 1, message = "Max tokens must be at least 1")
    @Max(value = 100000, message = "Max tokens must not exceed 100000")
    private Integer maxTokens;

    @DecimalMin(value = "0.0", message = "Temperature must be at least 0.0")
    @DecimalMax(value = "2.0", message = "Temperature must not exceed 2.0")
    private Double temperature;
}
