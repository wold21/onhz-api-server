package com.onhz.server.service.user;


import com.onhz.server.common.enums.ReviewType;
import com.onhz.server.common.enums.Role;
import com.onhz.server.common.utils.CommonUtils;
import com.onhz.server.common.utils.PageUtils;
import com.onhz.server.config.JwtConfig;
import com.onhz.server.dto.request.LoginRequest;
import com.onhz.server.dto.request.SignUpRequest;
import com.onhz.server.dto.request.UserRequest;
import com.onhz.server.dto.response.LoginResponse;
import com.onhz.server.dto.response.UserExistsResponse;
import com.onhz.server.dto.response.UserResponse;
import com.onhz.server.dto.response.review.ReviewResponse;
import com.onhz.server.entity.SessionEntity;
import com.onhz.server.entity.review.ReviewEntity;
import com.onhz.server.entity.user.UserEntity;
import com.onhz.server.repository.SessionRepository;
import com.onhz.server.repository.UserRepository;
import com.onhz.server.repository.dsl.ReviewDSLRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
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
    private final ReviewDSLRepository reviewDSLRepository;

    @Transactional
    public void signUp(SignUpRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }
        // 유저명 랜덤 처리
        String userName = CommonUtils.generateRandomUsername();
        UserEntity user = UserEntity.builder()
                .email(signUpRequest.getEmail())
                .userName(userName)
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .isSocial(false)
                .role(Role.USER)
                .build();

        userRepository.save(user);
    }

    @Transactional
    public LoginResponse login(LoginRequest loginRequest, HttpServletRequest request) {
        UserEntity user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
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
    public LoginResponse generateUserToken(UserEntity user, HttpServletRequest request) {
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

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .deviceId(deviceId)
                .user(UserResponse.from(user))
                .build();
    }

    @Transactional
    public void changeUserInfo(UserEntity requestUser, UserRequest userRequest) {
        UserEntity user = userRepository.findById(requestUser.getId()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        String encodedNewPassword = null;
        if(userRequest.getNewPassword() != null){
            encodedNewPassword = passwordEncoder.encode(userRequest.getNewPassword());
        }

        user.updateInfo(userRequest.getUserName(), encodedNewPassword);
    }

    public UserExistsResponse nameCheck(String userName) {
        String name = userName.replaceAll(" ", "");
        if(name.isBlank()){
            throw new IllegalArgumentException("유저명은 공백일 수 없습니다.");
        }

        UserEntity user = userRepository.findByUserName(name).orElse(null);
        if(user != null) {
            return UserExistsResponse.builder()
                    .available(false)
                    .build();
        } else {
            return UserExistsResponse.builder()
                    .available(true)
                    .build();
        }
    }

    public UserExistsResponse emailCheck(String userEmail) {
        String email = userEmail.replaceAll(" ", "");
        if (email.isBlank()) {
            throw new IllegalArgumentException("이메일은 공백일 수 없습니다.");
        }

        UserEntity user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            return UserExistsResponse.builder()
                    .available(false)
                    .build();
        } else {
            return UserExistsResponse.builder()
                    .available(true)
                    .build();
        }
    }

    public List<ReviewResponse> getUserReviews(Long userId, ReviewType reviewType, int offset, int limit, String orderBy) {
        Pageable pageable = PageUtils.createPageable(offset, limit, orderBy, ReviewEntity.class);
        return reviewDSLRepository.findUserReviews(reviewType, userId, pageable);
    }
}
