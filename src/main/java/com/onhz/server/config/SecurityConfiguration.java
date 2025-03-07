package com.onhz.server.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onhz.server.security.jwt.JwtAccessDeniedHandler;
import com.onhz.server.security.jwt.JwtAuthenticationEntryPoint;
import com.onhz.server.security.jwt.JwtAuthenticationFilter;
import com.onhz.server.security.oauth.OAuth2AuthenticationSuccessHandler;
import com.onhz.server.security.oauth.OAuth2AuthorizationCookieRepository;
import com.onhz.server.service.auth.OAuthUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity // Spring Security 활성화 및 SecurityFilterAutoConfiguration 구성
@Configuration
@Slf4j
public class SecurityConfiguration  {
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    private OAuthUserService oAuthUserService;
    private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private ObjectMapper objectMapper;
    private OAuth2AuthorizationCookieRepository oAuth2AuthorizationCookieRepository;

    public SecurityConfiguration(JwtAuthenticationFilter jwtAuthenticationFilter, OAuthUserService oAuthUserService, OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler, OAuth2AuthorizationCookieRepository oAuth2AuthorizationCookieRepository, ObjectMapper objectMapper) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.oAuthUserService = oAuthUserService;
        this.oAuth2AuthenticationSuccessHandler = oAuth2AuthenticationSuccessHandler;
        this.oAuth2AuthorizationCookieRepository = oAuth2AuthorizationCookieRepository;
        this.objectMapper = objectMapper;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Cross-Site Request Forgery 비활성화 REST API는 CSRF를 사용하지 않음.
                .cors(AbstractHttpConfigurer::disable) // Cross-Origin Resource Sharing 비활성화
                .formLogin(form -> form.disable())  // 기본 로그인 폼 비활성화
                .httpBasic(basic -> basic.disable()) // 기본 인증 비활성화
                .logout(AbstractHttpConfigurer::disable); // 기본 로그아웃 비활성화

        http
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // Spring Security가 세션을 생성하지 않음. 토큰기반이기 때문.

        http
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        http
                .authorizeHttpRequests(request -> { request
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers(
                            "/",
                            "/index.html",
                            "/main.html",
                            "/login/**",
                            "/login/oauth2/**",
                            "/oauth2/**",
                            "/api/v1/auth/**",
                            "/h2-console",
                            "/swagger-ui/**",
                            "/v3/api-docs/**").permitAll().anyRequest().authenticated();});

        http
                .oauth2Login(oauth2 -> oauth2
                .authorizationEndpoint(authorization -> authorization
                        .authorizationRequestRepository(oAuth2AuthorizationCookieRepository))
                .userInfoEndpoint(userInfo -> userInfo
                        .userService(oAuthUserService)
                )
                .successHandler(oAuth2AuthenticationSuccessHandler));
        http
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint(objectMapper))
                        .accessDeniedHandler(new JwtAccessDeniedHandler(objectMapper)));
        return http.build();
    }

}
