package com.apigraveyard.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreateTrackedApiRequest {

    @NotBlank(message = "API name is required")
    private String apiName;

    @NotBlank(message = "Base URL is required")
    private String baseUrl;

    private String healthCheckUrl;

    private String documentationUrl;

    @Pattern(regexp = "GET|POST|PUT|DELETE", message = "HTTP method must be GET, POST, PUT or DELETE")
    private String httpMethod = "GET";

    private Integer expectedStatus = 200;

    private Integer checkInterval = 3600;
}