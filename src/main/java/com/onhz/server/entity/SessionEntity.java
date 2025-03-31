package com.onhz.server.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_session_tb")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SessionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "refresh_token",nullable = false)
    private String refreshToken;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "device_info")
    private String deviceInfo;

    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;

    @Column(name = "access_ip")
    private String accessIp;

    @Builder
    public SessionEntity(Long userId, String refreshToken, LocalDateTime expiresAt, String deviceId, String deviceInfo, String accessIp) {
        this.userId = userId;
        this.refreshToken = refreshToken;
        this.expiresAt = expiresAt;
        this.deviceId = deviceId;
        this.deviceInfo = deviceInfo;
        this.accessIp = accessIp;
    }

    public void updateLastUsedAt() {
        this.lastUsedAt = LocalDateTime.now();
    }
}
