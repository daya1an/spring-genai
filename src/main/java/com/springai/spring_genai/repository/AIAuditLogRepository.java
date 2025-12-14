package com.springai.spring_genai.repository;

import com.springai.spring_genai.entity.AuditLogEntity;
import com.springai.spring_genai.entity.AuditStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AIAuditLogRepository extends JpaRepository<AuditLogEntity, Long> {

    List<AuditLogEntity> findByUserId(String userId);

    List<AuditLogEntity> findByStatus(AuditStatus status);

    Page<AuditLogEntity> findByUserIdOrderByTimestampDesc(String userId, Pageable pageable);

    List<AuditLogEntity> findByStatusAndTimestampBefore(AuditStatus status, LocalDateTime dateTime);

    @Query("SELECT COUNT(a) FROM AuditLogEntity a WHERE a.action = :action AND a.timestamp >= :startTime")
    long countActionsByTimeRange(@Param("action") String action, @Param("startTime") LocalDateTime startTime);

    @Query("SELECT a.action, COUNT(a) FROM AuditLogEntity a GROUP BY a.action ORDER BY COUNT(a) DESC")
    List<Object[]> getActionStats();

    @Query("SELECT a FROM AuditLogEntity a WHERE a.status = :status AND a.timestamp >= :startTime ORDER BY a.timestamp DESC")
    List<AuditLogEntity> findFailedAuditsSince(@Param("status") AuditStatus status, @Param("startTime") LocalDateTime startTime);

    Page<AuditLogEntity> findByAction(String action, Pageable pageable);
}
