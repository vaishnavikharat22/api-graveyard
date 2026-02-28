package com.apigraveyard.backend.scheduler;

import com.apigraveyard.backend.model.TrackedApi;
import com.apigraveyard.backend.repository.TrackedApiRepository;
import com.apigraveyard.backend.service.HealthCheckService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@RequiredArgsConstructor
@Slf4j
public class HealthCheckScheduler {

    private final TrackedApiRepository trackedApiRepository;
    private final HealthCheckService healthCheckService;

    // Thread pool - check multiple APIs in parallel
    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    // Runs every 60 seconds
    @Scheduled(fixedDelay = 60000)
    public void runDueChecks() {
        List<TrackedApi> dueApis = trackedApiRepository
                .findByIsActiveTrueAndNextCheckAtBefore(LocalDateTime.now());

        if (dueApis.isEmpty()) {
            log.debug("No APIs due for health check");
            return;
        }

        log.info("Running health checks for {} APIs", dueApis.size());

        for (TrackedApi api : dueApis) {
            executor.submit(() -> {
                try {
                    healthCheckService.performCheck(api);
                } catch (Exception e) {
                    log.error("Unexpected error checking API {}: {}",
                        api.getApiName(), e.getMessage());
                }
            });
        }
    }
}