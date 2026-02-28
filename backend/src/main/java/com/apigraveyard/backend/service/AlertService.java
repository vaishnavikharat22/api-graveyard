package com.apigraveyard.backend.service;

import com.apigraveyard.backend.model.Alert;
import com.apigraveyard.backend.model.HealthCheck;
import com.apigraveyard.backend.model.TrackedApi;
import com.apigraveyard.backend.model.enums.AlertType;
import com.apigraveyard.backend.model.enums.ApiStatus;
import com.apigraveyard.backend.model.enums.Severity;
import com.apigraveyard.backend.repository.AlertRepository;
import com.apigraveyard.backend.repository.TrackedApiRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertService {

    private final AlertRepository alertRepository;
    private final TrackedApiRepository trackedApiRepository;

    public void evaluateHealthCheck(TrackedApi api, HealthCheck check) {
        if (!check.getIsUp()) {
            // API failed - increment counter
            int failures = api.getConsecutiveFailures() + 1;
            api.setConsecutiveFailures(failures);

            // Alert fires after 3 consecutive failures
            if (failures == 3) {
                createAlert(
                    api,
                    AlertType.DOWN,
                    Severity.CRITICAL,
                    api.getApiName() + " is DOWN",
                    "API failed " + failures + " consecutive health checks. " +
                    "Last error: " + check.getErrorMessage()
                );
                api.setCurrentStatus(ApiStatus.DOWN);
                log.warn("ALERT CREATED: {} is DOWN", api.getApiName());
            } else if (failures == 1 && api.getCurrentStatus() == ApiStatus.ACTIVE) {
                // First failure - mark as degraded
                api.setCurrentStatus(ApiStatus.DEGRADED);
                createAlert(
                    api,
                    AlertType.DEGRADED,
                    Severity.MEDIUM,
                    api.getApiName() + " may be degraded",
                    "API failed 1 health check. Monitoring closely."
                );
            }
        } else {
            // API is up
            if (api.getCurrentStatus() == ApiStatus.DOWN ||
                api.getCurrentStatus() == ApiStatus.DEGRADED) {
                // API recovered!
                createAlert(
                    api,
                    AlertType.STATUS_CHANGE,
                    Severity.LOW,
                    api.getApiName() + " has recovered",
                    "API is back online after " + api.getConsecutiveFailures() + " failed checks."
                );
                log.info("RECOVERY: {} is back online", api.getApiName());
            }
            api.setConsecutiveFailures(0);
            api.setCurrentStatus(ApiStatus.ACTIVE);
        }

        trackedApiRepository.save(api);
    }

    private void createAlert(TrackedApi api, AlertType type,
                              Severity severity, String title, String description) {
        Alert alert = new Alert();
        alert.setTrackedApi(api);
        alert.setUser(api.getUser());
        alert.setAlertType(type);
        alert.setSeverity(severity);
        alert.setTitle(title);
        alert.setDescription(description);
        alert.setIsResolved(false);
        alertRepository.save(alert);
    }
}