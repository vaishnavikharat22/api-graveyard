package com.apigraveyard.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthCheckResponse {
    private Long checkId;
    private LocalDateTime checkedAt;
    private Integer httpStatus;
    private Integer responseTime;
    private Boolean isUp;
    private String errorMessage;
}