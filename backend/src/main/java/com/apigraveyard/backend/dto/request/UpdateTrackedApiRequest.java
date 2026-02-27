package com.apigraveyard.backend.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateTrackedApiRequest {

    private String apiName;
    private String baseUrl;
    private String healthCheckUrl;
    private String documentationUrl;

    @Pattern(regexp = "GET|POST|PUT|DELETE", message = "HTTP method must be GET, POST, PUT or DELETE")
    private String httpMethod;

    private Integer expectedStatus;
    private Integer checkInterval;
    private Boolean isActive;
}