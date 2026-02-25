package com.apigraveyard.backend.model;

import com.apigraveyard.backend.model.enums.AlertType;
import com.apigraveyard.backend.model.enums.Severity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "alerts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long alertId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "api_id", nullable = false)
    private TrackedApi trackedApi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AlertType alertType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Severity severity;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Boolean isResolved = false;

    private LocalDateTime resolvedAt;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "alert", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Notification> notifications;
}