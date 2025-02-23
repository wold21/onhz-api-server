package com.onhz.server.service.auth;

import com.onhz.server.config.JwtConfig;
import com.onhz.server.dto.TokenRefreshRequestDto;
import com.onhz.server.dto.TokenResponseDto;
import com.onhz.server.entity.SessionEntity;
import com.onhz.server.entity.UserEntity;
import com.onhz.server.repository.SessionRepository;
import com.onhz.server.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class JwtTokenService {
    private JwtConfig jwtConfig;
    private UserRepository userRepository;
    private SessionRepository sessionRepository;

    public TokenResponseDto refreshAccessToken(TokenRefreshRequestDto refreshRequestDto) {
        SessionEntity sessionEntity = sessionRepository
                .findByRefreshTokenAndDeviceId(refreshRequestDto.getRefreshToken(), refreshRequestDto.getDeviceId())
                .orElseThrow(() -> new RuntimeException("등록된 refresh 토큰이 없습니다."));

        if (sessionEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
            sessionRepository.delete(sessionEntity);
            throw new ExpiredJwtException(null, null, "Refresh 토큰이 만료되었습니다.");
        }

        sessionEntity.updateLastUsedAt();
        sessionRepository.save(sessionEntity);

        UserEntity userEntity = userRepository.findById(sessionEntity.getUserId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        String newAccessToken = jwtConfig.generateToken(userEntity.getEmail(), userEntity.getUserName());

        return TokenResponseDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshRequestDto.getRefreshToken())
                .deviceId(refreshRequestDto.getDeviceId())
                .build();
    }
}
