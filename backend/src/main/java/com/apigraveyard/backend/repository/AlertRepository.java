package com.apigraveyard.backend.repository;

import com.apigraveyard.backend.model.Alert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    Page<Alert> findByUserUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    Page<Alert> findByUserUserIdAndIsResolvedOrderByCreatedAtDesc(Long userId, Boolean isResolved, Pageable pageable);
    Long countByUserUserIdAndIsResolved(Long userId, Boolean isResolved);
    Long countByTrackedApiApiIdAndIsResolved(Long apiId, Boolean isResolved);
}