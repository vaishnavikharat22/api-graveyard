package com.apigraveyard.backend.service;

import com.apigraveyard.backend.dto.request.CreateTrackedApiRequest;
import com.apigraveyard.backend.dto.request.UpdateTrackedApiRequest;
import com.apigraveyard.backend.dto.response.TrackedApiResponse;
import com.apigraveyard.backend.model.TrackedApi;
import com.apigraveyard.backend.model.User;
import com.apigraveyard.backend.model.enums.ApiStatus;
import com.apigraveyard.backend.repository.TrackedApiRepository;
import com.apigraveyard.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrackedApiService {

    private final TrackedApiRepository trackedApiRepository;
    private final UserRepository userRepository;

    public TrackedApiResponse addApi(Long userId, CreateTrackedApiRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // If no healthCheckUrl provided, use baseUrl
        String healthCheckUrl = request.getHealthCheckUrl() != null
                ? request.getHealthCheckUrl()
                : request.getBaseUrl();

        TrackedApi api = new TrackedApi();
        api.setUser(user);
        api.setApiName(request.getApiName());
        api.setBaseUrl(request.getBaseUrl());
        api.setHealthCheckUrl(healthCheckUrl);
        api.setDocumentationUrl(request.getDocumentationUrl());
        api.setHttpMethod(request.getHttpMethod() != null ? request.getHttpMethod() : "GET");
        api.setExpectedStatus(request.getExpectedStatus() != null ? request.getExpectedStatus() : 200);
        api.setCheckInterval(request.getCheckInterval() != null ? request.getCheckInterval() : 3600);
        api.setCurrentStatus(ApiStatus.UNKNOWN);
        api.setIsActive(true);
        api.setConsecutiveFailures(0);
        api.setNextCheckAt(LocalDateTime.now().plusMinutes(1));

        TrackedApi saved = trackedApiRepository.save(api);
        return mapToResponse(saved);
    }

    public List<TrackedApiResponse> getAllApis(Long userId) {
        return trackedApiRepository.findByUserUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public TrackedApiResponse getApiById(Long userId, Long apiId) {
        TrackedApi api = trackedApiRepository.findByApiIdAndUserUserId(apiId, userId)
                .orElseThrow(() -> new RuntimeException("API not found or access denied"));
        return mapToResponse(api);
    }

    public TrackedApiResponse updateApi(Long userId, Long apiId, UpdateTrackedApiRequest request) {
        TrackedApi api = trackedApiRepository.findByApiIdAndUserUserId(apiId, userId)
                .orElseThrow(() -> new RuntimeException("API not found or access denied"));

        if (request.getApiName() != null) api.setApiName(request.getApiName());
        if (request.getBaseUrl() != null) api.setBaseUrl(request.getBaseUrl());
        if (request.getHealthCheckUrl() != null) api.setHealthCheckUrl(request.getHealthCheckUrl());
        if (request.getDocumentationUrl() != null) api.setDocumentationUrl(request.getDocumentationUrl());
        if (request.getHttpMethod() != null) api.setHttpMethod(request.getHttpMethod());
        if (request.getExpectedStatus() != null) api.setExpectedStatus(request.getExpectedStatus());
        if (request.getCheckInterval() != null) api.setCheckInterval(request.getCheckInterval());
        if (request.getIsActive() != null) api.setIsActive(request.getIsActive());

        TrackedApi updated = trackedApiRepository.save(api);
        return mapToResponse(updated);
    }

    public void deleteApi(Long userId, Long apiId) {
        TrackedApi api = trackedApiRepository.findByApiIdAndUserUserId(apiId, userId)
                .orElseThrow(() -> new RuntimeException("API not found or access denied"));
        trackedApiRepository.delete(api);
    }

    private TrackedApiResponse mapToResponse(TrackedApi api) {
        return TrackedApiResponse.builder()
                .apiId(api.getApiId())
                .apiName(api.getApiName())
                .baseUrl(api.getBaseUrl())
                .healthCheckUrl(api.getHealthCheckUrl())
                .documentationUrl(api.getDocumentationUrl())
                .httpMethod(api.getHttpMethod())
                .expectedStatus(api.getExpectedStatus())
                .checkInterval(api.getCheckInterval())
                .currentStatus(api.getCurrentStatus())
                .lastChecked(api.getLastChecked())
                .nextCheckAt(api.getNextCheckAt())
                .isActive(api.getIsActive())
                .createdAt(api.getCreatedAt())
                .build();
    }
}