package com.apigraveyard.backend.repository;

import com.apigraveyard.backend.model.Notification;
import com.apigraveyard.backend.model.enums.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByStatus(NotificationStatus status);
    List<Notification> findByUserUserIdOrderByCreatedAtDesc(Long userId);
}