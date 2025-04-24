package com.onhz.server.service.auth;

import com.onhz.server.config.JwtConfig;
import com.onhz.server.dto.response.TokenResponse;
import com.onhz.server.entity.SessionEntity;
import com.onhz.server.entity.user.UserEntity;
import com.onhz.server.repository.SessionRepository;
import com.onhz.server.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class JwtTokenService {
    private final JwtConfig jwtConfig;
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;

    public TokenResponse refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = null;
        String deviceId = request.getHeader("Device-Id");
        if(deviceId == null || deviceId.isEmpty()) {
            throw new RuntimeException("Device-Id가 없습니다.");
        }

        Cookie[] cookies = request.getCookies();
        if(cookies != null){
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }
        if (refreshToken == null) {
            throw new RuntimeException("Refresh 토큰이 없습니다.");
        }

        SessionEntity sessionEntity = sessionRepository
                .findByRefreshTokenAndDeviceId(refreshToken, deviceId)
                .orElseThrow(() -> new RuntimeException("등록된 refresh 토큰이 없습니다."));

        if (sessionEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
            sessionRepository.delete(sessionEntity);
            Cookie expiredCookie = new Cookie("refreshToken", "");
            expiredCookie.setMaxAge(0);
            expiredCookie.setPath("/");
            expiredCookie.setHttpOnly(true);
            expiredCookie.setSecure(true);
            response.addCookie(expiredCookie);
            throw new ExpiredJwtException(null, null, "Refresh 토큰이 만료되었습니다.");
        }

        sessionEntity.updateLastUsedAt();
        sessionRepository.save(sessionEntity);

        UserEntity userEntity = userRepository.findById(sessionEntity.getUserId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        String newAccessToken = jwtConfig.generateToken(userEntity.getEmail(), userEntity.getUserName());

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .deviceId(deviceId)
                .build();
    }
}
