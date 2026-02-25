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

    private Boolean emailEnabled = true;
    private Boolean inAppEnabled = true;
    private Boolean alertOnDown = true;
    private Boolean alertOnDegraded = true;
    private Boolean alertOnDeprecation = true;
    private Boolean alertOnResponseChange = false;
}