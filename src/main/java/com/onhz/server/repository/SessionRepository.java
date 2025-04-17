package com.onhz.server.repository;

import com.onhz.server.entity.SessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<SessionEntity, Long> {
    Optional<SessionEntity> findByRefreshTokenAndDeviceId(String refreshToken, String deviceId);
    Optional<SessionEntity> findByDeviceId(String deviceId);
    List<SessionEntity> findByExpiresAtBefore(LocalDateTime time);
}
