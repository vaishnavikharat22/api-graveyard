package com.apigraveyard.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notification_preferences")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long prefId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Builder.Default
    private Boolean emailEnabled = true;
    @Builder.Default
private Boolean inAppEnabled = true;
@Builder.Default
private Boolean alertOnDown = true;
@Builder.Default
private Boolean alertOnDegraded = true;
@Builder.Default
private Boolean alertOnDeprecation = true;
@Builder.Default
private Boolean alertOnResponseChange = false;
}