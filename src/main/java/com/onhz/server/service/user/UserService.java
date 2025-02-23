package com.onhz.server.service.user;


import com.onhz.server.common.Role;
import com.onhz.server.config.JwtConfig;
import com.onhz.server.dto.LoginRequestDto;
import com.onhz.server.dto.PasswordChangeRequestDto;
import com.onhz.server.dto.SignUpRequestDto;
import com.onhz.server.dto.TokenResponseDto;
import com.onhz.server.entity.SessionEntity;
import com.onhz.server.entity.UserEntity;
import com.onhz.server.repository.SessionRepository;
import com.onhz.server.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    @Value("${spring.service.jwt.refresh-token-expiration}")
    private Long refreshTokenExpiration;
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtConfig jwtConfig;

    @Transactional
    public void signUp(SignUpRequestDto signUpRequestDto) {
        if (userRepository.existsByEmail(signUpRequestDto.getEmail())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }
        if (userRepository.existsByUserName(signUpRequestDto.getUserName())) {
            throw new IllegalArgumentException("이미 사용중인 유저명입니다.");
        }

        UserEntity user = UserEntity.builder()
                .email(signUpRequestDto.getEmail())
                .userName(signUpRequestDto.getUserName())
                .password(passwordEncoder.encode(signUpRequestDto.getPassword()))
                .isSocial(false)
                .role(Role.USER)
                .build();

        userRepository.save(user);
    }

    @Transactional
    public TokenResponseDto login(LoginRequestDto loginRequestDto, HttpServletRequest request) {
        UserEntity user = userRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }

        return generateUserToken(user, request);
    }

    @Transactional
    public void logout(String deviceId, HttpServletRequest request) {

        SessionEntity sessionEntity = sessionRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("로그아웃할 기기를 찾을 수 없습니다."));

        sessionRepository.delete(sessionEntity);
    }

    @Transactional
    public TokenResponseDto generateUserToken(UserEntity user, HttpServletRequest request) {
        String accessToken = jwtConfig.generateToken(user.getEmail(), user.getUserName());
        String refreshToken = jwtConfig.generateRefreshToken(user.getEmail(), user.getUserName());
        String deviceId = UUID.randomUUID().toString();

        Date now = new Date();
        Date validity = new Date(now.getTime() + refreshTokenExpiration);
        LocalDateTime expiresAt = LocalDateTime.ofInstant(validity.toInstant(), ZoneId.systemDefault());

        SessionEntity sessionEntity = SessionEntity.builder()
                .userId(user.getId())
                .deviceId(deviceId)
                .refreshToken(refreshToken)
                .expiresAt(expiresAt)
                .deviceInfo(request.getHeader("user-agent"))
                .accessIp(request.getRemoteAddr())
                .build();
        sessionRepository.save(sessionEntity);

        return TokenResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .deviceId(deviceId)
                .build();
    }

    @Transactional
    public void changePassword(String email, PasswordChangeRequestDto pcDto) {
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

        if(!passwordEncoder.matches(pcDto.getPassword(), user.getPassword())){
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }
        String encodedNewPassword = passwordEncoder.encode(pcDto.getNewPassword());
        user.updatePassword(encodedNewPassword);
    }
}
