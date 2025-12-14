package com.springai.spring_genai.repository;

import com.springai.spring_genai.entity.AIRequestEntity;
import com.springai.spring_genai.entity.RequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface GenAIRequestRepository extends JpaRepository<AIRequestEntity, Long> {

    List<AIRequestEntity> findByUserId(String userId);

    List<AIRequestEntity> findByStatus(RequestStatus status);

    Page<AIRequestEntity> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);

    List<AIRequestEntity> findByStatusAndCreatedAtBefore(RequestStatus status, LocalDateTime dateTime);

    @Query("SELECT COUNT(a) FROM AIRequestEntity a WHERE a.userId = :userId AND a.createdAt >= :startDate")
    long countRequestsByUserAndDate(@Param("userId") String userId, @Param("startDate") LocalDateTime startDate);

    @Query("SELECT AVG(a.processingTimeMs) FROM AIRequestEntity a WHERE a.userId = :userId")
    Optional<Double> getAverageProcessingTimeByUser(@Param("userId") String userId);

    @Query("SELECT a.model, COUNT(a) FROM AIRequestEntity a GROUP BY a.model")
    List<Object[]> getModelUsageStats();

    @Query("SELECT a FROM AIRequestEntity a WHERE a.userId = :userId AND a.model = :model ORDER BY a.createdAt DESC")
    Page<AIRequestEntity> findByUserAndModel(@Param("userId") String userId, @Param("model") String model, Pageable pageable);
}
