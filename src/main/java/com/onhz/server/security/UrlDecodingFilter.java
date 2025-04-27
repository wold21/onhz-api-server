package com.onhz.server.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE) // 다른 필터보다 먼저 실행되도록 설정
public class UrlDecodingFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        UrlDecodedRequestWrapper wrappedRequest = new UrlDecodedRequestWrapper(request);
        filterChain.doFilter(wrappedRequest, response);
    }

    private static class UrlDecodedRequestWrapper extends ContentCachingRequestWrapper {
        public UrlDecodedRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        @Override
        public String getParameter(String name) {
            String value = super.getParameter(name);
            return decodeUrlParam(value);
        }

        @Override
        public String[] getParameterValues(String name) {
            String[] values = super.getParameterValues(name);
            if (values != null) {
                for (int i = 0; i < values.length; i++) {
                    values[i] = decodeUrlParam(values[i]);
                }
            }
            return values;
        }

        private String decodeUrlParam(String value) {
            if (value != null) {
                try {
                    if (value.contains("%")) {
                        String decoded = URLDecoder.decode(value, StandardCharsets.UTF_8);
                        return decoded;
                    }
                } catch (Exception e) {
                    throw new RuntimeException("URL 디코딩 실패", e);
                }
            }
            return value;
        }
    }
}
