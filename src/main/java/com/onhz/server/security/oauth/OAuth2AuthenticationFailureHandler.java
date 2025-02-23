package com.onhz.server.security.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onhz.server.exception.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class OAuth2AuthenticationFailureHandler implements AuthenticationFailureHandler {
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.error("Social login failed: ", exception);

        ErrorResponse errorResponse = new ErrorResponse(
                "소셜 로그인 처리 중 오류가 발생했습니다.",
                HttpStatus.UNAUTHORIZED,
                HttpStatus.UNAUTHORIZED.value()
        );

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(errorResponse.getStatusCode());
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
