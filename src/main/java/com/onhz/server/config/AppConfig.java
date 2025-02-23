package com.onhz.server.config;

import com.onhz.server.security.oauth.OAuth2AuthorizationCookieRepository;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;

@Configuration
public class AppConfig {
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() { //configure
        return (web) -> web.ignoring()
                .requestMatchers(PathRequest.toH2Console())
                .requestMatchers("/error", "/favicon.ico", "/img/**", "/css/**", "/js/**");
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder(); // 내부적으로 여러 해싱 알고리즘을 지원하고 사용자의 비밀번호에 대해 적절한 알고리즘을 선택하는 PasswordEncoder이다.
    }

    @Bean
    public OAuth2AuthorizationRequestResolver authorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository) {
        return new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, "/oauth2/authorization");
    }

    @Bean
    public OAuth2AuthorizationCookieRepository oAuth2AuthorizationCookieRepository() {
        return new OAuth2AuthorizationCookieRepository();
    }
}
