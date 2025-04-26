package com.onhz.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import java.util.function.Consumer;

@Configuration
public class OAuth2ClientConfig {
    @Bean
    public OAuth2AuthorizationRequestResolver oAuth2AuthorizationRequestResolver(
            ClientRegistrationRepository clientRegistrationRepository) {

        DefaultOAuth2AuthorizationRequestResolver resolver = new DefaultOAuth2AuthorizationRequestResolver(
                clientRegistrationRepository, "/oauth2/authorization");

        resolver.setAuthorizationRequestCustomizer(customizeOAuth2AuthorizationRequest());

        return resolver;
    }

    private Consumer<OAuth2AuthorizationRequest.Builder> customizeOAuth2AuthorizationRequest() {
        return customizer -> {
            String clientId = customizer.build().getClientId();

            // 구글 로그인 커스터마이징
            if (clientId.contains("google")) {
                customizer.additionalParameters(params -> {
                    params.put("prompt", "select_account");
                    params.put("access_type", "offline");
                });
            }
            // 네이버 로그인 커스터마이징
            else if (clientId.contains("naver")) {
                customizer.additionalParameters(params -> {
                    params.put("auth_type", "reprompt");
                });
            }
            // 카카오 로그인 커스터마이징
            else if (clientId.contains("kakao")) {
                customizer.additionalParameters(params -> {
                    params.put("prompt", "login");
                });
            }
        };
    }
}
