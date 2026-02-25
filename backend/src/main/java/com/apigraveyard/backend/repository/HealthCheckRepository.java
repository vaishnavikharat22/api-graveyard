package com.apigraveyard.backend.repository;

import com.apigraveyard.backend.model.HealthCheck;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HealthCheckRepository extends JpaRepository<HealthCheck, Long> {
    Page<HealthCheck> findByTrackedApiApiIdOrderByCheckedAtDesc(Long apiId, Pageable pageable);

    List<HealthCheck> findByTrackedApiApiIdAndCheckedAtBetween(
        Long apiId, LocalDateTime from, LocalDateTime to
    );

    @Query("SELECT AVG(h.responseTime) FROM HealthCheck h WHERE h.trackedApi.apiId = :apiId AND h.checkedAt >= :since AND h.isUp = true")
    Double findAvgResponseTimeSince(Long apiId, LocalDateTime since);

    @Query("SELECT COUNT(h) FROM HealthCheck h WHERE h.trackedApi.apiId = :apiId AND h.checkedAt >= :since AND h.isUp = true")
    Long countSuccessfulChecksSince(Long apiId, LocalDateTime since);

    @Query("SELECT COUNT(h) FROM HealthCheck h WHERE h.trackedApi.apiId = :apiId AND h.checkedAt >= :since")
    Long countTotalChecksSince(Long apiId, LocalDateTime since);
}