package com.springai.spring_genai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModelInfo {
    private String provider;
    private String model;
    private String version;
    private String status;
    private String description;
}
