package com.onhz.server.security.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onhz.server.dto.response.LoginResponse;
import com.onhz.server.dto.response.TokenResponse;
import com.onhz.server.entity.user.UserEntity;
import com.onhz.server.repository.UserRepository;
import com.onhz.server.service.user.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final UserService userService;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        Long userId = oauth2User.getAttribute("userId");
        UserEntity user = userRepository.findByIdWithSocial(userId)
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 회원입니다."));

        LoginResponse loginResponse =  userService.generateUserToken(user, request, response);
        ObjectMapper objectMapper = new ObjectMapper();
        String userJson = objectMapper.writeValueAsString(loginResponse.getUser());

        response.setContentType("text/html;charset=UTF-8");

        String html = String.format("""
            <html>
            <body>
                <script>
                    window.opener.postMessage({
                        type: 'oauth2Success',
                        accessToken: '%s',
                        deviceId: '%s',
                        user: %s
                    }, '*');
                    window.close();
                </script>
            </body>
            </html>
            """,
                loginResponse.getAccessToken(),
                loginResponse.getDeviceId(),
                userJson
        );

        response.getWriter().write(html);
    }
}
