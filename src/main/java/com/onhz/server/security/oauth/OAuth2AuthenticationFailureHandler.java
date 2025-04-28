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
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
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
        String errorMessage = "소셜 로그인 처리 중 오류가 발생했습니다.";

        if (exception instanceof OAuth2AuthenticationException) {
            if (exception.getMessage() != null && !exception.getMessage().isBlank()) {
                errorMessage = exception.getMessage();
            }
        }

        ErrorResponse errorResponse = ErrorResponse.of(
                errorMessage,
                HttpStatus.UNAUTHORIZED
        );

        String redirectUrl = "/oauth/error?message=" + errorMessage + "&status="+ HttpStatus.UNAUTHORIZED.value();

        response.setContentType("text/html;charset=UTF-8");
        String html = String.format("""
            <html>
            <body>
                <script>
                    // 부모 창에서 에러 페이지로 리다이렉트
                    window.opener.location.href = '%s';
                    // 팝업 창 닫기
                    window.close();
                </script>
            </body>
            </html>
            """, redirectUrl);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(errorResponse.getStatusCode());
        response.getWriter().write(html);
    }
}
