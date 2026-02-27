package com.apigraveyard.backend.dto.response;

import com.apigraveyard.backend.model.enums.ApiStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackedApiResponse {
    private Long apiId;
    private String apiName;
    private String baseUrl;
    private String healthCheckUrl;
    private String documentationUrl;
    private String httpMethod;
    private Integer expectedStatus;
    private Integer checkInterval;
    private ApiStatus currentStatus;
    private LocalDateTime lastChecked;
    private LocalDateTime nextCheckAt;
    private Boolean isActive;
    private LocalDateTime createdAt;
}