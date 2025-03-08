package com.onhz.server.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onhz.server.security.jwt.JwtAccessDeniedHandler;
import com.onhz.server.security.jwt.JwtAuthenticationEntryPoint;
import com.onhz.server.security.jwt.JwtAuthenticationFilter;
import com.onhz.server.security.oauth.OAuth2AuthenticationSuccessHandler;
import com.onhz.server.security.oauth.OAuth2AuthorizationCookieRepository;
import com.onhz.server.service.auth.OAuthUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@EnableWebSecurity // Spring Security 활성화 및 SecurityFilterAutoConfiguration 구성
@Configuration
@Slf4j
public class SecurityConfiguration  {

    @Value("${spring.profiles.active}")
    private String activeProfile;
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
                .cors(cors -> cors
                        .configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
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
                            "/login/**",
                            "/login/oauth2/**",
                            "/oauth2/**",
//                            "/api/v1/auth/**",
                            "/api/v1/**",
                            "/swagger-ui/**",
                            "/v3/api-docs/**").permitAll();
                    if (activeProfile.equals("local")) {
                        request.requestMatchers("/h2-console/**").permitAll();
                    }
                    request.anyRequest().authenticated();});

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

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOriginPattern("*");

        /* NGINX 오픈 시 사용 */
//        configuration.setAllowedOrigins(Arrays.asList(
//                "http://localhost:3000"
//        ));

        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "refreshToken",
                "Device-Id"
        ));
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization"
        ));
//        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
