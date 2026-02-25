package com.apigraveyard.backend.repository;

import com.apigraveyard.backend.model.TrackedApi;
import com.apigraveyard.backend.model.enums.ApiStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TrackedApiRepository extends JpaRepository<TrackedApi, Long> {
    List<TrackedApi> findByUserUserId(Long userId);
    List<TrackedApi> findByIsActiveTrueAndNextCheckAtBefore(LocalDateTime now);
    Optional<TrackedApi> findByApiIdAndUserUserId(Long apiId, Long userId);
    List<TrackedApi> findByUserUserIdAndCurrentStatus(Long userId, ApiStatus status);
}