package com.apigraveyard.backend.controller;

import com.apigraveyard.backend.dto.response.HealthCheckResponse;
import com.apigraveyard.backend.model.HealthCheck;
import com.apigraveyard.backend.repository.HealthCheckRepository;
import com.apigraveyard.backend.repository.TrackedApiRepository;
import com.apigraveyard.backend.security.JwtUtil;
import com.apigraveyard.backend.service.HealthCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/tracked-apis")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HealthCheckController {

    private final HealthCheckRepository healthCheckRepository;
    private final TrackedApiRepository trackedApiRepository;
    private final HealthCheckService healthCheckService;
    private final JwtUtil jwtUtil;

    private Long getUserIdFromToken(String authHeader) {
        return jwtUtil.extractUserId(authHeader.substring(7));
    }

    @GetMapping("/{apiId}/health-history")
    public ResponseEntity<Map<String, Object>> getHealthHistory(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long apiId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Long userId = getUserIdFromToken(authHeader);

        // Verify ownership
        trackedApiRepository.findByApiIdAndUserUserId(apiId, userId)
                .orElseThrow(() -> new RuntimeException("API not found or access denied"));

        Page<HealthCheck> checks = healthCheckRepository
                .findByTrackedApiApiIdOrderByCheckedAtDesc(
                    apiId, PageRequest.of(page, size));

        Map<String, Object> response = new HashMap<>();
        response.put("content", checks.getContent().stream().map(this::mapToResponse).toList());
        response.put("totalElements", checks.getTotalElements());
        response.put("page", page);
        response.put("size", size);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{apiId}/check-now")
    public ResponseEntity<HealthCheckResponse> checkNow(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long apiId) {

        Long userId = getUserIdFromToken(authHeader);

        var api = trackedApiRepository.findByApiIdAndUserUserId(apiId, userId)
                .orElseThrow(() -> new RuntimeException("API not found or access denied"));

        HealthCheck result = healthCheckService.performCheck(api);
        return ResponseEntity.ok(mapToResponse(result));
    }

    private HealthCheckResponse mapToResponse(HealthCheck check) {
        return HealthCheckResponse.builder()
                .checkId(check.getCheckId())
                .checkedAt(check.getCheckedAt())
                .httpStatus(check.getHttpStatus())
                .responseTime(check.getResponseTime())
                .isUp(check.getIsUp())
                .errorMessage(check.getErrorMessage())
                .build();
    }
}