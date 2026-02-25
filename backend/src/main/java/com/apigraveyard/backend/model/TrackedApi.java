package com.apigraveyard.backend.model;

import com.apigraveyard.backend.model.enums.ApiStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tracked_apis")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackedApi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long apiId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 200)
    private String apiName;

    @Column(nullable = false, length = 500)
    private String baseUrl;

    @Column(length = 500)
    private String documentationUrl;

    @Column(length = 500)
    private String healthCheckUrl;

    @Column(length = 10)
    private String httpMethod = "GET";

    private Integer expectedStatus = 200;

    private Integer checkInterval = 3600;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ApiStatus currentStatus = ApiStatus.UNKNOWN;

    private LocalDateTime lastChecked;

    private LocalDateTime nextCheckAt;

    @Column(nullable = false)
    private Boolean isActive = true;

    private Integer consecutiveFailures = 0;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "trackedApi", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HealthCheck> healthChecks;

    @OneToMany(mappedBy = "trackedApi", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Alert> alerts;
}