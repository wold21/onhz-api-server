package com.onhz.server.config;

import com.onhz.server.interceptor.SessionUpdateInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer{
    private final SessionUpdateInterceptor sessionUpdateInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sessionUpdateInterceptor)
                .excludePathPatterns(
                        "/",
                        "/index.html",
                        "/main.html",
                        "/login/**",
                        "/login/oauth2/**",
                        "/oauth2/**",
                        "/api/auth/**",
                        "/h2-console",
                        "/static/**"
                );
    }
}
