package com.springai.spring_genai.config;

import com.springai.spring_genai.annotation.Auditable;
import com.springai.spring_genai.entity.AuditLogEntity;
import com.springai.spring_genai.entity.AuditStatus;
import com.springai.spring_genai.repository.AIAuditLogRepository;
import com.springai.spring_genai.utils.ValidationUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Arrays;

@Aspect
@Component
@Order(2)
@Slf4j
public class AuditAspect {

    private final AIAuditLogRepository auditLogRepository;
    private final AIProperties appProperties;
    private final ValidationUtils validationUtils;

    public AuditAspect(AIAuditLogRepository auditLogRepository, AIProperties appProperties, ValidationUtils validationUtils) {
        this.auditLogRepository = auditLogRepository;
        this.appProperties = appProperties;
        this.validationUtils = validationUtils;
    }

    @Around("@annotation(auditable)")
    public Object audit(ProceedingJoinPoint pjp, Auditable auditable) throws Throwable {

        if (!appProperties.getAudit().isEnabled()) {
            return pjp.proceed();
        }

        long startTime = System.currentTimeMillis();
        String userId = getCurrentUserId();
        String ipAddress = getClientIpAddress();
        HttpServletRequest request = getHttpServletRequest();

        AuditLogEntity auditLog = AuditLogEntity.builder()
                .userId(userId)
                .action(auditable.value())
                .endpoint(pjp.getSignature().toString())
                .ipAddress(ipAddress)
                .requestData(Arrays.toString(pjp.getArgs()))
                .timestamp(LocalDateTime.now())
                .build();

        try {
            Object result = pjp.proceed();

            long duration = System.currentTimeMillis() - startTime;
            auditLog.setStatus(AuditStatus.SUCCESS);
            auditLog.setResponseData(result != null ? result.toString() : "null");
            auditLog.setProcessingTimeMs(duration);

            log.info("Audit - Action: {}, User: {}, Status: SUCCESS, Duration: {}ms",
                    auditable.value(), userId, duration);

            auditLogRepository.save(auditLog);
            return result;

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;

            auditLog.setStatus(AuditStatus.FAILED);
            auditLog.setErrorMessage(e.getMessage());
            auditLog.setProcessingTimeMs(duration);

            log.error("Audit - Action: {}, User: {}, Status: FAILED, Error: {}",
                    auditable.value(), userId, e.getMessage());

            auditLogRepository.save(auditLog);
            throw e;
        }
    }

    private String getCurrentUserId() {
        try {
            HttpServletRequest request = getHttpServletRequest();
            if (request != null && request.getUserPrincipal() != null) {
                return request.getUserPrincipal().getName();
            }
        } catch (Exception e) {
            log.debug("Unable to retrieve current user", e);
        }
        return "UNKNOWN";
    }

    private String getClientIpAddress() {
        try {
            HttpServletRequest request = getHttpServletRequest();
            if (request != null) {
                String ip = request.getHeader("X-Forwarded-For");
                if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getRemoteAddr();
                }
                if (validationUtils.isValidIpAddress(ip)) {
                    return ip;
                }
            }
        } catch (Exception e) {
            log.debug("Unable to retrieve client IP", e);
        }
        return "UNKNOWN";
    }

    private HttpServletRequest getHttpServletRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            return attributes.getRequest();
        }
        return null;
    }
}
