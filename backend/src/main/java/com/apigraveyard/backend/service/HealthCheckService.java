package com.apigraveyard.backend.service;

import com.apigraveyard.backend.model.HealthCheck;
import com.apigraveyard.backend.model.TrackedApi;
import com.apigraveyard.backend.repository.HealthCheckRepository;
import com.apigraveyard.backend.repository.TrackedApiRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class HealthCheckService {

    private final HealthCheckRepository healthCheckRepository;
    private final TrackedApiRepository trackedApiRepository;
    private final AlertService alertService;
    private final RestTemplate restTemplate;

    public HealthCheck performCheck(TrackedApi api) {
        log.debug("Checking API: {} -> {}", api.getApiName(), api.getHealthCheckUrl());

        long startTime = System.currentTimeMillis();
        HealthCheck healthCheck = new HealthCheck();
        healthCheck.setTrackedApi(api);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    api.getHealthCheckUrl(),
                    HttpMethod.valueOf(api.getHttpMethod()),
                    null,
                    String.class
            );

            int statusCode = response.getStatusCode().value();
            long responseTime = System.currentTimeMillis() - startTime;
            boolean isUp = statusCode == api.getExpectedStatus();

            healthCheck.setHttpStatus(statusCode);
            healthCheck.setResponseTime((int) responseTime);
            healthCheck.setIsUp(isUp);

            if (response.getBody() != null) {
                healthCheck.setResponseHash(sha256(response.getBody()));
            }

            log.debug("API {} returned {} in {}ms", api.getApiName(), statusCode, responseTime);

        } catch (Exception e) {
            long responseTime = System.currentTimeMillis() - startTime;
            healthCheck.setIsUp(false);
            healthCheck.setResponseTime((int) responseTime);

            String errorMsg = e.getMessage();
            if (errorMsg != null && errorMsg.length() > 500) {
                errorMsg = errorMsg.substring(0, 500);
            }
            healthCheck.setErrorMessage(errorMsg);

            log.warn("API {} check failed: {}", api.getApiName(), errorMsg);
        }

        // Save health check result
        HealthCheck saved = healthCheckRepository.save(healthCheck);

        // Update last checked time and schedule next check
        api.setLastChecked(LocalDateTime.now());
        api.setNextCheckAt(LocalDateTime.now().plusSeconds(api.getCheckInterval()));
        trackedApiRepository.save(api);

        // Evaluate and create alerts if needed
        alertService.evaluateHealthCheck(api, saved);

        return saved;
    }

    private String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            return null;
        }
    }
}