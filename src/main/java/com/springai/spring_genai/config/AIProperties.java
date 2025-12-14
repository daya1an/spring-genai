package com.springai.spring_genai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.ai")
@Data
public class AIProperties {

    private AI ai = new AI();
    private API api = new API();
    private Security security = new Security();
    private Audit audit = new Audit();
    private Validation validation = new Validation();

    @Data
    public static class AI {
        private Request request = new Request();

        @Data
        public static class Request {
            private int timeout;
        }
    }

    @Data
    public static class API {
        private RateLimit rateLimit = new RateLimit();

        @Data
        public static class RateLimit {
            private int requests;
            private int durationMinutes;
        }
    }

    @Data
    public static class Security {
        private JWT jwt = new JWT();

        @Data
        public static class JWT {
            private String secret;
            private long expiration;
        }
    }

    @Data
    public static class Audit {
        private boolean enabled;
    }

    @Data
    public static class Validation {
        private boolean enableSanitization;
    }
}
