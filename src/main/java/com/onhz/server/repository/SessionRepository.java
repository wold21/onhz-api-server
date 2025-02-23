package com.onhz.server.repository;

import com.onhz.server.entity.SessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<SessionEntity, Long> {
    Optional<SessionEntity> findByRefreshTokenAndDeviceId(String refreshToken, String deviceId);
    Optional<SessionEntity> findByDeviceId(String deviceId);
}
