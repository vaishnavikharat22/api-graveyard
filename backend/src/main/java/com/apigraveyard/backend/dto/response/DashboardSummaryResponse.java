package com.apigraveyard.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryResponse {
    private Long totalApis;
    private Long activeApis;
    private Long degradedApis;
    private Long downApis;
    private Long openAlerts;
    private Long criticalAlerts;
}