package com.apigraveyard.backend.controller;

import com.apigraveyard.backend.dto.response.DashboardSummaryResponse;
import com.apigraveyard.backend.model.enums.ApiStatus;
import com.apigraveyard.backend.repository.AlertRepository;
import com.apigraveyard.backend.repository.TrackedApiRepository;
import com.apigraveyard.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DashboardController {

    private final TrackedApiRepository trackedApiRepository;
    private final AlertRepository alertRepository;
    private final JwtUtil jwtUtil;

    private Long getUserIdFromToken(String authHeader) {
        return jwtUtil.extractUserId(authHeader.substring(7));
    }

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryResponse> getSummary(
            @RequestHeader("Authorization") String authHeader) {

        Long userId = getUserIdFromToken(authHeader);

        long totalApis = trackedApiRepository.findByUserUserId(userId).size();
        long activeApis = trackedApiRepository
            .findByUserUserIdAndCurrentStatus(userId, ApiStatus.ACTIVE).size();
        long degradedApis = trackedApiRepository
            .findByUserUserIdAndCurrentStatus(userId, ApiStatus.DEGRADED).size();
        long downApis = trackedApiRepository
            .findByUserUserIdAndCurrentStatus(userId, ApiStatus.DOWN).size();
        long openAlerts = alertRepository
            .countByUserUserIdAndIsResolved(userId, false);

        DashboardSummaryResponse response = DashboardSummaryResponse.builder()
                .totalApis(totalApis)
                .activeApis(activeApis)
                .degradedApis(degradedApis)
                .downApis(downApis)
                .openAlerts(openAlerts)
                .build();

        return ResponseEntity.ok(response);
    }
}