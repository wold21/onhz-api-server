package com.onhz.server.service.auth;

import com.onhz.server.common.utils.CommonUtils;
import com.onhz.server.dto.OAuthAttributesDto;
import com.onhz.server.entity.SocialEntity;
import com.onhz.server.entity.user.UserEntity;
import com.onhz.server.repository.SocialRepository;
import com.onhz.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OAuthUserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;
    private final SocialRepository socialRepository;
    private final PasswordEncoder passwordEncoder;

    private static final Map<String, String> PROVIDER_ATTRIBUTE_KEYS = Map.of(
            "google", "sub",
            "kakao", "id",
            "naver", "response"
    );


    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        try {
            return processOAuth2User(userRequest, oauth2User);
        } catch (Exception e) {
            log.error("소셜 로그인 실패", e);
            throw new OAuth2AuthenticationException( new OAuth2Error("소셜 로그인 에러 발생", e.getMessage(), null),
                    e.getMessage()
            );
        }
    }
    @Transactional
    protected OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        /*
        * oauth2User 객체에서 필요한 정보 추출
        */
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = extractUserNameAttributeName(userRequest);
        String accessToken = userRequest.getAccessToken().getTokenValue();

        /*
        * 프로바이더 마다 제공되는 데이터 형식이 달라서 해당 부분 맵핑 작업
        */
        OAuthAttributesDto attributes = OAuthAttributesDto.of(
                registrationId,
                userNameAttributeName,
                oauth2User.getAttributes(),
                accessToken
        );

        /*
        * 우리 쪽 DB에 회원 가입 및 이메일 중복 확인
        */
        UserEntity savedUser = saveOrUpdate(attributes, registrationId);
        return createOAuth2User(oauth2User, savedUser, registrationId);
    }
    private String extractUserNameAttributeName(OAuth2UserRequest userRequest) {
        return userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();
    }

    @Transactional
    protected OAuth2User createOAuth2User(OAuth2User oauth2User, UserEntity savedUser, String registrationId) {
        Map<String, Object> attributes = new HashMap<>(oauth2User.getAttributes());
        attributes.put("provider", registrationId);
        attributes.put("userId", savedUser.getId());

        return new DefaultOAuth2User(
                oauth2User.getAuthorities(),
                attributes,
                PROVIDER_ATTRIBUTE_KEYS.getOrDefault(registrationId, "sub")
        );
    }

    @Transactional
    protected UserEntity saveOrUpdate(OAuthAttributesDto attributes, String registrationId) {
        try {
            SocialEntity social = socialRepository.findByCode(registrationId.toLowerCase())
                    .orElseThrow(() -> new RuntimeException("사용할 수 없는 플랫폼입니다."));

            return userRepository.findByEmail(attributes.getEmail())
                    .map(entity -> {
                        if(!entity.isSocial()) {
                            throw new RuntimeException("일반 회원으로 가입된 이메일입니다.");
                        }
                        return entity;
                    })
                    .orElseGet(() -> {
                        // 유저명 랜덤처리
                        String userName = CommonUtils.generateRandomString(10);
                        UserEntity newUser = UserEntity.oauth2Builder()
                                .email(attributes.getEmail())
                                .userName(userName)
                                .password(passwordEncoder.encode("oauth2"))
                                .social(social)
                                .socialAccessToken(attributes.getAccsessToken())
                                .profilePath(null)
                                .build();
                        return userRepository.save(newUser);
                    });
        } catch (Exception e) {
            log.error("Error in saveOrUpdate", e);
            throw e;
        }
    }
}
