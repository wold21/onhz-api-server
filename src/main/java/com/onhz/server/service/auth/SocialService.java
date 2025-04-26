package com.onhz.server.service.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onhz.server.entity.user.UserEntity;
import com.onhz.server.entity.user.UserSocialEntity;
import com.onhz.server.event.SocialUnlinkEvent;
import com.onhz.server.exception.ErrorCode;
import com.onhz.server.exception.SocialDisconnectionException;
import com.onhz.server.repository.UserSocialSessionRepository;
import com.onhz.server.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SocialService {
    private static final String DISCONNECT_ERROR_TEMPLATE =
            "%s 연동 해제에 실패했습니다.\n토큰 만료로 인해 자동 처리가 불가능합니다.\n%s 계정 설정에서 직접 연동을 해제해 주시기 바랍니다.";
    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String naverClientId;
    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String naverClientSecret;
    @Value("${spring.service.provider.kakao.admin-key}")
    private String kakaoAdminKey;
    private final UserSocialSessionRepository userSocialRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher eventPublisher;

    private String createErrorMessage(String provider) {
        return String.format(DISCONNECT_ERROR_TEMPLATE, provider, provider);
    }
    @Transactional
    public void disconnectGoogle(UserSocialEntity userSocial) {
        try {
            String accessToken = userSocial.getAccessToken();

            if (accessToken == null || accessToken.isEmpty()) {
                log.error("구글 연동 해제 실패: 액세스 토큰이 없음");
                userSocialRepository.delete(userSocial);
                throw new SocialDisconnectionException(ErrorCode.NOT_FOUND_EXCEPTION, createErrorMessage("구글"));
            }

            log.info("구글 연동 해제 시도");
            boolean disconnected = requestGoogleDisconnection(accessToken);

            userSocialRepository.delete(userSocial);

            if (disconnected) {
                log.info("구글 연동 해제 성공");
            } else {
                log.warn("구글 연동 해제 API 호출 실패. 사용자에게 직접 해제 안내 필요");
                throw new SocialDisconnectionException(ErrorCode.NOT_FOUND_EXCEPTION, createErrorMessage("구글"));
            }
        } catch (SocialDisconnectionException e) {
            throw e;
        } catch (Exception e) {
            log.error("구글 연동 해제 중 오류 발생", e);
            userSocialRepository.delete(userSocial);
            throw new SocialDisconnectionException(ErrorCode.NOT_FOUND_EXCEPTION, createErrorMessage("구글"));
        }
    }

    @Transactional
    public void disconnectNaver(UserSocialEntity userSocial) {
        try {
            String accessToken = userSocial.getAccessToken();
            String refreshToken = userSocial.getRefreshToken();
            boolean disconnected = false;

            if (refreshToken != null && !refreshToken.isEmpty()) {
                log.info("리프레시 토큰으로 네이버 액세스 토큰 갱신 시도");
                String newAccessToken = refreshNaverToken(refreshToken);
                if (newAccessToken != null) {
                    disconnected = requestNaverDisconnection(newAccessToken);
                } else {
                    log.error("리프레시 토큰 갱신 실패");
                    userSocialRepository.delete(userSocial);
                    throw new SocialDisconnectionException(ErrorCode.NOT_FOUND_EXCEPTION, createErrorMessage("네이버"));
                }
            } else {
                log.info("리프레시 토큰 없음. 기존 액세스 토큰으로 네이버 연동 해제 시도.");
                disconnected = requestNaverDisconnection(accessToken);
            }

            if (disconnected) {
                userSocialRepository.delete(userSocial);
                log.info("네이버 연동 해제 성공");
            } else {
                log.error("네이버 연동 해제 실패");
                userSocialRepository.delete(userSocial);
                throw new SocialDisconnectionException(ErrorCode.NOT_FOUND_EXCEPTION, createErrorMessage("네이버"));
            }
        } catch (SocialDisconnectionException e) {
            throw e;
        } catch (Exception e) {
            log.error("네이버 연동 해제 중 오류 발생", e);
            userSocialRepository.delete(userSocial);
            throw new SocialDisconnectionException(ErrorCode.NOT_FOUND_EXCEPTION, createErrorMessage("네이버"));
        }
    }

    @Transactional
    public void disconnectKakao(UserSocialEntity userSocial) {
        try {
            // socialId 값 확인
            String socialId = userSocial.getSocialId();
            if (socialId == null || socialId.isEmpty()) {
                log.error("카카오 연동 해제 실패: social_id가 없음");
                userSocialRepository.delete(userSocial);
                throw new SocialDisconnectionException(ErrorCode.NOT_FOUND_EXCEPTION, createErrorMessage("카카오"));
            }
            boolean disconnected = requestKakaoAdminDisconnection(socialId);
            userSocialRepository.delete(userSocial);

            if (disconnected) {
                log.info("카카오 연동 해제 성공. 소셜 ID: {}", socialId);
            } else {
                log.warn("카카오 Admin API 연동 해제 실패. 사용자에게 직접 해제 안내 필요");
                throw new SocialDisconnectionException(ErrorCode.NOT_FOUND_EXCEPTION, createErrorMessage("카카오"));
            }
        } catch (SocialDisconnectionException e) {
            throw e;
        } catch (Exception e) {
            log.error("카카오 연동 해제 중 오류 발생", e);
            userSocialRepository.delete(userSocial);
            throw new SocialDisconnectionException(ErrorCode.NOT_FOUND_EXCEPTION, createErrorMessage("카카오"));
        }
    }

    private boolean requestGoogleDisconnection(String accessToken) {
        try {
            String revokeUrl = UriComponentsBuilder.fromHttpUrl("https://oauth2.googleapis.com/revoke")
                    .queryParam("token", accessToken)
                    .build()
                    .toUriString();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            log.debug("구글 연동 해제 요청. 토큰: {}...",
                    accessToken.substring(0, Math.min(10, accessToken.length())));

            ResponseEntity<String> response = restTemplate.exchange(
                    revokeUrl,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                log.debug("구글 연동 해제 API 호출 성공");
                return true;
            } else {
                log.error("구글 연동 해제 API 오류 응답: Status={}", response.getStatusCode());
                return false;
            }
        } catch (Exception e) {
            log.error("구글 연동 해제 중 예상치 못한 오류", e);
            return false;
        }
    }

    private boolean requestKakaoAdminDisconnection(String socialId) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "KakaoAK " + kakaoAdminKey);
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("target_id_type", "user_id");
            body.add("target_id", socialId);

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

            log.debug("카카오 Admin API 연동 해제 요청. 소셜 ID: {}", socialId);
            ResponseEntity<String> response = restTemplate.exchange(
                    "https://kapi.kakao.com/v1/user/unlink",
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                log.debug("카카오 Admin API 응답: {}", response.getBody());
                return true;
            } else {
                log.error("카카오 Admin API 오류 응답: Status={}, Body={}", response.getStatusCode(), response.getBody());
                return false;
            }
        } catch (Exception e) {
            log.error("카카오 Admin API 호출 중 예상치 못한 오류", e);
            return false;
        }
    }



    private String refreshNaverToken(String refreshToken) {
        try {
            String refreshUrl = UriComponentsBuilder.fromHttpUrl("https://nid.naver.com/oauth2.0/token")
                    .queryParam("grant_type", "refresh_token")
                    .queryParam("client_id", naverClientId)
                    .queryParam("client_secret", naverClientSecret)
                    .queryParam("refresh_token", refreshToken)
                    .build()
                    .toUriString();

            ResponseEntity<String> response = restTemplate.exchange(
                    refreshUrl,
                    HttpMethod.GET,
                    null,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                String newAccessToken = jsonNode.path("access_token").asText(null);
                if(newAccessToken != null && !newAccessToken.isEmpty()) {
                    log.info("네이버 액세스 토큰 갱신 성공");
                    return newAccessToken;
                }
            } else {
                log.error("네이버 토큰 갱신 실패: Status={}, Body={}", response.getStatusCode(), response.getBody());
                String msg = createErrorMessage("네이버");
                throw new SocialDisconnectionException(ErrorCode.NOT_FOUND_EXCEPTION, msg);
            }
        } catch (Exception e) {
            log.error("네이버 토큰 갱신 중 오류 발생", e);
            String msg = createErrorMessage("네이버");
            throw new SocialDisconnectionException(ErrorCode.NOT_FOUND_EXCEPTION, msg);
        }
        return null;
    }

    private boolean requestNaverDisconnection(String accessToken) {
        try {
            String revokeUrl = UriComponentsBuilder.fromHttpUrl("https://nid.naver.com/oauth2.0/token")
                    .queryParam("grant_type", "delete")
                    .queryParam("client_id", naverClientId)
                    .queryParam("client_secret", naverClientSecret)
                    .queryParam("access_token", accessToken)
                    .build()
                    .toUriString();

            ResponseEntity<String> response = restTemplate.exchange(
                    revokeUrl,
                    HttpMethod.GET,
                    null,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                boolean success = "success".equals(jsonNode.path("result").asText());
                if (success) {
                    log.info("네이버 연동 해제 성공");
                    return true;
                }
            } else {
                log.error("네이버 연동 해제 API 호출 실패: Status={}, Body={}", response.getStatusCode(), response.getBody());
            }
        } catch (Exception e) {
            log.error("네이버 연동 해제 중 오류 발생", e);
        }
        return false;
    }

    @Transactional
    public void unlinkSocial(String socialId) {
        UserSocialEntity userSocial = userSocialRepository.findBySocialId(socialId)
                .orElseThrow(() -> new SocialDisconnectionException(ErrorCode.NOT_FOUND_EXCEPTION, "소셜 계정이 존재하지 않습니다."));
        UserEntity user = userSocial.getUser();
        userSocialRepository.delete(userSocial);
        eventPublisher.publishEvent(new SocialUnlinkEvent(this, user, true));
    }

    /**
     * 네이버 서명 검증
     */
    public boolean verifyNaverSignature(String signature, String clientId, String userId, String expires, String timestamp) {
        try {
            String message = "client_id=" + clientId + "&user_id=" + userId + "&expires=" + expires + "&timestamp=" + timestamp;

            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(naverClientSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKey);
            byte[] hmacData = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));

            String calculatedSignature = Base64.getEncoder().encodeToString(hmacData);

            return signature.equals(calculatedSignature);
        } catch (Exception e) {
            log.error("네이버 서명 검증 중 오류 발생", e);
            return false;
        }
    }
}
