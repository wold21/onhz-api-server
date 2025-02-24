package com.onhz.server.service.auth;

import com.onhz.server.entity.SessionEntity;
import com.onhz.server.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class SessionService {
    private final SessionRepository sessionRepository;

    @Transactional
    public void updateLastAccessedAt(String deviceId) {
        SessionEntity sessionEntity = sessionRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new RuntimeException("세션을 찾을 수 없습니다."));
        sessionEntity.updateLastUsedAt();
    }
}
