package com.onhz.server.service.user;


import com.onhz.server.common.enums.ReviewType;
import com.onhz.server.common.enums.Role;
import com.onhz.server.common.utils.CommonUtils;
import com.onhz.server.common.utils.FileManager;
import com.onhz.server.common.utils.PageUtils;
import com.onhz.server.config.JwtConfig;
import com.onhz.server.dto.request.LoginRequest;
import com.onhz.server.dto.request.SignUpRequest;
import com.onhz.server.dto.request.UserInfoRequest;
import com.onhz.server.dto.response.LoginResponse;
import com.onhz.server.dto.response.SummaryResponse;
import com.onhz.server.dto.response.UserExistsResponse;
import com.onhz.server.dto.response.UserResponse;
import com.onhz.server.dto.response.common.NoticeResponse;
import com.onhz.server.dto.response.review.ReviewResponse;
import com.onhz.server.entity.SessionEntity;
import com.onhz.server.entity.review.ReviewEntity;
import com.onhz.server.entity.review.ReviewLikeEntity;
import com.onhz.server.entity.user.UserDeletedEntity;
import com.onhz.server.entity.user.UserEntity;
import com.onhz.server.entity.user.UserRatingSummaryEntity;
import com.onhz.server.entity.user.UserSocialEntity;
import com.onhz.server.event.SocialUnlinkEvent;
import com.onhz.server.exception.ErrorCode;
import com.onhz.server.exception.FileBusinessException;
import com.onhz.server.exception.NotFoundException;
import com.onhz.server.repository.*;
import com.onhz.server.repository.dsl.ReviewDSLRepository;
import com.onhz.server.service.auth.SocialService;
import com.onhz.server.service.common.EmailService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    @Value("${spring.service.jwt.refresh-token-expiration}")
    private Long refreshTokenExpiration;
    @Value("${spring.service.file.base-path}")
    private String basePath;
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtConfig jwtConfig;
    private final ReviewDSLRepository reviewDSLRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final ReviewRepository reviewRepository;
    private final FileManager fileManager;
    private final UserDeletedRepository userDeletedRepository;
    private final UserRatingSummaryRepository userRatingSummaryRepository;
    private final EmailService emailService;
    private final UserSocialSessionRepository userSocialSessionRepository;
    private final SocialService socialService;
    private final RestTemplate restTemplate;
    private final NicknameService nicknameService;

    @Transactional
    public void signUp(SignUpRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }
        // 유저명 랜덤 처리
        String userName = getRandomUserName();
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
    public LoginResponse login(LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {
        UserEntity user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }

        return generateUserToken(user, request, response);
    }

    @Transactional
    public void logout(String deviceId, HttpServletRequest request) {

        SessionEntity sessionEntity = sessionRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("로그아웃할 기기를 찾을 수 없습니다."));

        // 소셜 유저일 시 로그아웃(카카오에 한함)
        UserEntity user = userRepository.findById(sessionEntity.getUserId()).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_EXCEPTION, "유저를 찾을 수 없습니다."));
        UserSocialEntity userSocial = userSocialSessionRepository.findByUserId(sessionEntity.getUserId()).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_EXCEPTION, "유저를 찾을 수 없습니다."));
        if(user.isSocial() && user.getSocial().getCode().equals("kakao")) {
            socialLogout(userSocial);
        }
        sessionRepository.delete(sessionEntity);
    }

    @Transactional
    public LoginResponse generateUserToken(UserEntity user, HttpServletRequest request, HttpServletResponse response) {
        String accessToken = jwtConfig.generateToken(user.getEmail(), user.getUserName());
        String refreshToken = jwtConfig.generateRefreshToken(user.getEmail(), user.getUserName());
        String deviceId = UUID.randomUUID().toString();

        Date now = new Date();
        Date validity = new Date(now.getTime() + refreshTokenExpiration);
        LocalDateTime expiresAt = LocalDateTime.ofInstant(validity.toInstant(), ZoneId.systemDefault());
        int cookieMaxAge = (int) ((validity.getTime() - now.getTime()) / 1000);


        SessionEntity sessionEntity = SessionEntity.builder()
                .userId(user.getId())
                .deviceId(deviceId)
                .refreshToken(refreshToken)
                .expiresAt(expiresAt)
                .deviceInfo(request.getHeader("user-agent"))
                .accessIp(request.getRemoteAddr())
                .build();
        sessionRepository.save(sessionEntity);

        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setSecure(true); // 운영시 true
        refreshTokenCookie.setHttpOnly(true);
//        refreshTokenCookie.setDomain("onhz.kr");
        refreshTokenCookie.setMaxAge(cookieMaxAge);

        response.addCookie(refreshTokenCookie);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .deviceId(deviceId)
                .user(UserResponse.from(user))
                .build();
    }

    @Transactional
    public UserResponse changeUserInfo(UserEntity requestUser, UserInfoRequest userInfoRequest) {
        if(userInfoRequest.getUserName() == null){
            throw new IllegalArgumentException("유저명은 공백일 수 없습니다.");
        }
        UserEntity user = getUser(requestUser.getId());
        String encodedNewPassword = null;
        if(userInfoRequest.getNewPassword() != null){
            encodedNewPassword = passwordEncoder.encode(userInfoRequest.getNewPassword());
        }

        user.updateInfo(userInfoRequest.getUserName(), encodedNewPassword);
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse changeUserImage(UserEntity requestUser, MultipartFile file) {
        UserEntity user = getUser(requestUser.getId());
        try{
            log.info("File Upload Start");
            // user 경로
            String userPath = "profile/" + user.getId().toString();
            log.info("userPath: {}", userPath);
            // 파일 업로드 경로 셋팅
            Path uploadPath = Path.of(basePath, userPath);
            // 경로에 폴더 없으면 생성
            Path directoryPath = fileManager.createFolder(uploadPath);
            // 저장할 UUID 파일명 생성
            String saveFileName = fileManager.getSaveFileName(file.getOriginalFilename());
            // 저장할 파일 경로(폴더 경로 + 파일명)
            Path savePath = Paths.get(directoryPath.toString(), saveFileName);
            // 업로드
            fileManager.uploadFile(savePath, file.getInputStream());
            log.info("파일 업로드 완료. 저장 경로: {}", savePath);
            log.info("파일 존재 확인: {}", Files.exists(savePath));
            // 기존 파일 삭제
            if(user.getProfilePath() != null){
                fileManager.deleteFile(basePath + user.getProfilePath());
            }
            // 유저 정보 수정을 위한 경로 생성
            Path relativePath = Path.of(basePath).relativize(savePath);
            // 유저 정보 수정
            user.updateProfile(relativePath.toString());
            return UserResponse.from(user);
        } catch (IOException e){
            throw new FileBusinessException(ErrorCode.FILE_BUSINESS_EXCEPTION, "파일 업로드 중 오류가 발생했습니다.");
        }
    }

    @Transactional
    public void deleteUser(UserEntity requestUser) {
        UserEntity user = getUser(requestUser.getId());
        userDeletion(user);
    }

    @Transactional
    public void deleteUserById(Long userId) {
        UserEntity user = getUser(userId);
        userDeletion(user);
    }

    @EventListener
    public void handleSocialUnlinkEvent(SocialUnlinkEvent event) {
        userDeletion(event.getUser(), event.isSocialUnlink());
    }

    public void userDeletion(UserEntity user) {
        userDeletion(user, false);
    }
    @Transactional
    public void userDeletion(UserEntity user, boolean isUnlink) {
        UserEntity deleteUser = userRepository.findByEmail("deleteUser").orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));
        // 요청자가 소셜 공급자가 아닌데 소셜 유저인 경우 연동해제 작업
        if(!isUnlink && user.isSocial()){
            disconnectSocialAccount(user);
        }

        // 유저 프로필 삭제
        if(user.getProfilePath() != null){
            fileManager.deleteFile(basePath + user.getProfilePath());
        }

        // 유저 리뷰 삭제 유저로 변경
        List<ReviewEntity> reviews = reviewRepository.findByUserId(user.getId());
        for(ReviewEntity review : reviews){
            review.updateUser(deleteUser);
        }

        // 해당 유저 좋아요 삭제
        reviewLikeRepository.deleteByUserId(user.getId());

        // 삭제 대상 유저 del_tb로 insert
        UserDeletedEntity deletedUser = UserDeletedEntity.fromUser(user);
        userDeletedRepository.save(deletedUser);

        // 삭제 대상 유저 삭제
        userRepository.delete(user);
    }


    public UserExistsResponse nameCheck(String userName) {
        String name = userName.replaceAll(" ", "");
        if(name.isBlank()){
            throw new IllegalArgumentException("유저명은 공백일 수 없습니다.");
        }

        UserEntity user = userRepository.findByUserName(name);
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

    public List<ReviewResponse> getUserReviews(Long userId, ReviewType reviewType, Long lastId, String lastOrderValue, int limit, String orderBy) {
        Pageable pageable = PageUtils.createPageable(0, limit, orderBy, ReviewEntity.class);
        return reviewDSLRepository.findUserReviewsByCursor(reviewType, userId, lastId, lastOrderValue, pageable);
    }

    public UserEntity getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
    }

    public SummaryResponse getUserRatings(Long userId) {
        UserRatingSummaryEntity userRatingSummary = userRatingSummaryRepository.findByUserId(userId);
        return SummaryResponse.from(userRatingSummary);
    }

    public List<ReviewResponse> getUserLikeReviews(Long userId, Long lastId, String lastOrderValue, int limit, String orderBy) {
        Pageable pageable = PageUtils.createPageable(0, limit, orderBy, ReviewEntity.class);
        List<ReviewResponse> reviewLike = reviewDSLRepository.findReviewsByUserId(userId, lastId, lastOrderValue, pageable);
        return reviewLike;
    }

    @Transactional
    public NoticeResponse forgotPassword(String email, String userName) {
        UserEntity user = userRepository.findByEmailAndUserName(email, userName).orElseThrow(() -> new IllegalArgumentException("해당 정보의 유저가 존재하지 않습니다."));
        if (user.isSocial()) {
            throw new IllegalArgumentException("소셜 로그인 유저는 비밀번호 찾기를 지원하지 않습니다.");
        }
        String tempPassword = CommonUtils.generateRandomString(12);
        user.updateInfo(user.getUserName(), passwordEncoder.encode(tempPassword));
        emailService.sendPasswordResetEmail(email, userName, tempPassword);
        return NoticeResponse.of("등록된 이메일로 임시 비밀번호가 전송되었습니다. 확인 후 로그인 해주세요.");
    }

    public void disconnectSocialAccount(UserEntity user) {
        try {
            String providerType = user.getSocial().getCode();
            UserSocialEntity userSocial = userSocialSessionRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new IllegalArgumentException("소셜 계정 정보가 존재하지 않습니다."));
            switch (providerType.toUpperCase()) {
                case "GOOGLE":
                    socialService.disconnectGoogle(userSocial);
                    break;
                case "NAVER":
                    socialService.disconnectNaver(userSocial);
                    break;
                case "KAKAO":
                    socialService.disconnectKakao(userSocial);
                    break;
                default:
                    throw new IllegalArgumentException("지원하지 않는 소셜 플랫폼입니다.");
            }
        } catch (Exception e) {
            log.error("소셜 계정 연동 해제 중 오류", e.getMessage());
            throw new RuntimeException("소셜 계정 연동 해제 중 오류가 발생했습니다.");
        }
    }

    private void socialLogout(UserSocialEntity user) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + user.getAccessToken());
            HttpEntity<String> entity = new HttpEntity<>(headers);

            restTemplate.exchange(
                    "https://kapi.kakao.com/v1/user/logout",
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            log.info("카카오 로그아웃 성공");
        } catch (Exception e) {
            log.error("카카오 로그아웃 실패", e);
        }
    }

    private String getRandomUserName() {
        String randomUserName = nicknameService.getRandomNickname();

        UserEntity existsUser = userRepository.findByUserName(randomUserName);
        if( existsUser != null) {
            return getRandomUserName();
        } else {
            return randomUserName;
        }
    }
}
