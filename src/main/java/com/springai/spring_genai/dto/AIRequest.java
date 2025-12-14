package com.springai.spring_genai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AIRequest {

    @NotBlank(message = "Prompt cannot be blank")
    @Schema(example = "Who are you ?")
    private String prompt;

    @Schema(example = "English")
    private String targetLanguage;

    @Schema(example = "100")
    private Integer maxTokens;

    @Schema(example = "0.7")
    private Double temperature;
}
