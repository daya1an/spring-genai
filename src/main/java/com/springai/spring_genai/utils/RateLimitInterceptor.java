package com.springai.spring_genai.utils;

import com.springai.spring_genai.config.AIProperties;
import com.springai.spring_genai.exception.RateLimitExceededException;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class RateLimitInterceptor implements HandlerInterceptor {

    private final Map<String, Bucket> userBuckets = new ConcurrentHashMap<>();
    private final AIProperties aiProperties;

    public RateLimitInterceptor(AIProperties aiProperties) {
        this.aiProperties = aiProperties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {

        String userId = request.getUserPrincipal() != null ?
                request.getUserPrincipal().getName() : "anonymous";

        Bucket bucket = userBuckets.computeIfAbsent(userId, k -> createBucket());

        if (bucket.tryConsume(1)) {
            log.debug("Rate limit check passed for user: {}", userId);
            response.addHeader("X-RateLimit-Remaining", String.valueOf(bucket.estimateAbilityToConsume(1).getRemainingTokens()));
            return true;
        }

        log.warn("Rate limit exceeded for user: {}", userId);
        throw new RateLimitExceededException(
                "Rate limit exceeded. Maximum " + aiProperties.getApi().getRateLimit().getRequests() +
                        " requests per " + aiProperties.getApi().getRateLimit().getDurationMinutes() + " minute(s)"
        );
    }

    private Bucket createBucket() {
        Bandwidth limit = Bandwidth.classic(
                aiProperties.getApi().getRateLimit().getRequests(),
                Refill.intervally(
                        aiProperties.getApi().getRateLimit().getRequests(),
                        Duration.ofMinutes(aiProperties.getApi().getRateLimit().getDurationMinutes())
                )
        );
        return Bucket4j.builder()
                .addLimit(limit)
                .build();
    }
}