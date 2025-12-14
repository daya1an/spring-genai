package com.springai.spring_genai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.ai")
@Data
public class AIProperties {

    private Request request = new Request();

    @Data
    public static class Request {
        private int timeout;
    }
}
