package com.apigraveyard.backend.dto.response;

import com.apigraveyard.backend.model.enums.AlertType;
import com.apigraveyard.backend.model.enums.Severity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertResponse {
    private Long alertId;
    private Long apiId;
    private String apiName;
    private AlertType alertType;
    private Severity severity;
    private String title;
    private String description;
    private Boolean isResolved;
    private LocalDateTime resolvedAt;
    private LocalDateTime createdAt;
}