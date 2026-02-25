package com.apigraveyard.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "health_checks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long checkId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "api_id", nullable = false)
    private TrackedApi trackedApi;

    @CreationTimestamp
    private LocalDateTime checkedAt;

    private Integer httpStatus;

    private Integer responseTime;

    private Boolean isUp;

    @Column(length = 500)
    private String errorMessage;

    @Column(length = 64)
    private String responseHash;
}