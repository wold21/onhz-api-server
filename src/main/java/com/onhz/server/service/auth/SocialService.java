package com.onhz.server.service.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onhz.server.entity.user.UserSocialEntity;
import com.onhz.server.repository.UserSocialSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
@Slf4j
public class SocialService {
    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String naverClientId;
    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String naverClientSecret;
    private final UserSocialSessionRepository userSocialRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    public void disconnectGoogle(UserSocialEntity userSocial) {
        // Google API를 사용하여 계정 연동 해제
        // Google API 호출 코드 작성
    }

    public void disconnectNaver(UserSocialEntity userSocial) {
        String accessToken = userSocial.getAccessToken();
        String refreshToken = userSocial.getRefreshToken();

        if (refreshToken != null && !refreshToken.isEmpty()) {
            log.info("리프레시 토큰으로 네이버 액세스 토큰 갱신 시도");
            String newAccessToken = refreshNaverToken(refreshToken);
            if (newAccessToken != null) {
                boolean disconnected = requestNaverDisconnection(newAccessToken);
                if (disconnected) {
                    userSocialRepository.delete(userSocial);
                }
            } else {
                log.warn("네이버 토큰 갱신 실패. 기존 액세스 토큰으로 연동 해제 시도.");
            }
        } else {
            log.info("리프레시 토큰 없음. 기존 액세스 토큰으로 네이버 연동 해제 시도.");
        }
    }

    public void disconnectKakao(UserSocialEntity userSocial) {
        // Kakao API를 사용하여 계정 연동 해제
        // Kakao API 호출 코드 작성
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
                if (newAccessToken != null) {
                    log.info("네이버 액세스 토큰 갱신 성공");
                    return newAccessToken;
                } else {
                    log.warn("네이버 토큰 갱신 응답에 액세스 토큰 없음: {}", response.getBody());
                }
            } else {
                log.error("네이버 토큰 갱신 실패: Status={}, Body={}", response.getStatusCode(), response.getBody());
            }
        } catch (Exception e) {
            log.error("네이버 토큰 갱신 중 오류 발생", e);
        }
        return null; // 갱신 실패 시 null 반환
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
                } else {
                    log.warn("네이버 연동 해제 API 응답 실패: {}", response.getBody());
                }
            } else {
                log.error("네이버 연동 해제 API 호출 실패: Status={}, Body={}", response.getStatusCode(), response.getBody());
            }
        } catch (Exception e) {
            log.error("네이버 연동 해제 중 오류 발생", e);
        }
        return false; // 연동 해제 실패
    }
}
