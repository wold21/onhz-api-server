package com.onhz.server.dto;

import com.onhz.server.common.utils.OAuthUtils;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.collections4.MapUtils;

import java.util.HashMap;
import java.util.Map;

@Getter
public class OAuthAttributesDto {
    private final Map<String, Object> attributes;
    private final String nameAttributeKey;
    private final String name;
    private final String email;
    private final String accsessToken;

    @Builder
    public OAuthAttributesDto(Map<String, Object> attributes,String nameAttributeKey, String name, String email, String accessToken) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
        this.accsessToken = accessToken;
    }

    public static OAuthAttributesDto of(String registrationId, String userNameAttributeName, Map<String, Object> attributes, String accessToken) {
        if ("google".equals(registrationId)) {
            return ofGoogle(userNameAttributeName, attributes, accessToken);
        } else if ("naver".equals(registrationId)) {
            return ofNaver(userNameAttributeName, attributes, accessToken);
        } else if ("kakao".equals(registrationId)){
            return ofKakao(userNameAttributeName, attributes, accessToken);
        }
        throw new IllegalArgumentException("지원하지 않는 로그인 형식입니다.");
    }

    private static OAuthAttributesDto ofGoogle(String userNameAttributeName, Map<String, Object> attributes, String accessToken) {
        return OAuthAttributesDto.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .accessToken(accessToken)
                .build();
    }

    private static OAuthAttributesDto ofKakao(String userNameAttributeName, Map<String, Object> attributes, String accessToken) {
        Map<String, Object> kakaoAccount = OAuthUtils.extractMap(attributes, "kakao_account");
        Map<String, Object> profile = OAuthUtils.extractMap(attributes, "properties");
        return OAuthAttributesDto.builder()
                .name(MapUtils.getString(profile, "nickname").replaceAll(" ", ""))
                .email(MapUtils.getString(kakaoAccount, "email"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .accessToken(accessToken)
                .build();
    }
    private static OAuthAttributesDto ofNaver(String userNameAttributeName, Map<String, Object> attributes, String accessToken){
        Map<String, Object> naverInfo = OAuthUtils.extractMap(attributes, "response");
        return OAuthAttributesDto.builder()
                .name(MapUtils.getString(naverInfo, "name"))
                .email(MapUtils.getString(naverInfo, "email"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .accessToken(accessToken)
                .build();
    }
}
