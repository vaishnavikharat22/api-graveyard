package com.apigraveyard.backend.controller;

import com.apigraveyard.backend.dto.response.AlertResponse;
import com.apigraveyard.backend.model.Alert;
import com.apigraveyard.backend.repository.AlertRepository;
import com.apigraveyard.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/alerts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AlertController {

    private final AlertRepository alertRepository;
    private final JwtUtil jwtUtil;

    private Long getUserIdFromToken(String authHeader) {
        return jwtUtil.extractUserId(authHeader.substring(7));
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAlerts(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Boolean resolved) {

        Long userId = getUserIdFromToken(authHeader);
        Pageable pageable = PageRequest.of(page, size);
        Page<Alert> alerts;

        if (resolved != null) {
            alerts = alertRepository.findByUserUserIdAndIsResolvedOrderByCreatedAtDesc(
                userId, resolved, pageable);
        } else {
            alerts = alertRepository.findByUserUserIdOrderByCreatedAtDesc(userId, pageable);
        }

        Long unreadCount = alertRepository.countByUserUserIdAndIsResolved(userId, false);

        Map<String, Object> response = new HashMap<>();
        response.put("content", alerts.getContent().stream().map(this::mapToResponse).toList());
        response.put("totalElements", alerts.getTotalElements());
        response.put("page", page);
        response.put("size", size);
        response.put("unreadCount", unreadCount);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{alertId}/resolve")
    public ResponseEntity<AlertResponse> resolveAlert(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long alertId) {

        Long userId = getUserIdFromToken(authHeader);

        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alert not found"));

        if (!alert.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        alert.setIsResolved(true);
        alert.setResolvedAt(LocalDateTime.now());
        alertRepository.save(alert);

        return ResponseEntity.ok(mapToResponse(alert));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(
            @RequestHeader("Authorization") String authHeader) {
        Long userId = getUserIdFromToken(authHeader);
        Long count = alertRepository.countByUserUserIdAndIsResolved(userId, false);
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    private AlertResponse mapToResponse(Alert alert) {
        return AlertResponse.builder()
                .alertId(alert.getAlertId())
                .apiId(alert.getTrackedApi().getApiId())
                .apiName(alert.getTrackedApi().getApiName())
                .alertType(alert.getAlertType())
                .severity(alert.getSeverity())
                .title(alert.getTitle())
                .description(alert.getDescription())
                .isResolved(alert.getIsResolved())
                .resolvedAt(alert.getResolvedAt())
                .createdAt(alert.getCreatedAt())
                .build();
    }
}