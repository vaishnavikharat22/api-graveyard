package com.apigraveyard.backend.controller;

import com.apigraveyard.backend.dto.request.CreateTrackedApiRequest;
import com.apigraveyard.backend.dto.request.UpdateTrackedApiRequest;
import com.apigraveyard.backend.dto.response.TrackedApiResponse;
import com.apigraveyard.backend.security.JwtUtil;
import com.apigraveyard.backend.service.TrackedApiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tracked-apis")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TrackedApiController {

    private final TrackedApiService trackedApiService;
    private final JwtUtil jwtUtil;

    private Long getUserIdFromToken(String authHeader) {
        String token = authHeader.substring(7);
        return jwtUtil.extractUserId(token);
    }

    @PostMapping
    public ResponseEntity<TrackedApiResponse> addApi(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody CreateTrackedApiRequest request) {
        Long userId = getUserIdFromToken(authHeader);
        TrackedApiResponse response = trackedApiService.addApi(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<TrackedApiResponse>> getAllApis(
            @RequestHeader("Authorization") String authHeader) {
        Long userId = getUserIdFromToken(authHeader);
        List<TrackedApiResponse> response = trackedApiService.getAllApis(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{apiId}")
    public ResponseEntity<TrackedApiResponse> getApiById(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long apiId) {
        Long userId = getUserIdFromToken(authHeader);
        TrackedApiResponse response = trackedApiService.getApiById(userId, apiId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{apiId}")
    public ResponseEntity<TrackedApiResponse> updateApi(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long apiId,
            @Valid @RequestBody UpdateTrackedApiRequest request) {
        Long userId = getUserIdFromToken(authHeader);
        TrackedApiResponse response = trackedApiService.updateApi(userId, apiId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{apiId}")
    public ResponseEntity<Void> deleteApi(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long apiId) {
        Long userId = getUserIdFromToken(authHeader);
        trackedApiService.deleteApi(userId, apiId);
        return ResponseEntity.noContent().build();
    }
}