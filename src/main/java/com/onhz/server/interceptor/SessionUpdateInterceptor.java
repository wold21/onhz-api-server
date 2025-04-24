package com.onhz.server.interceptor;

import com.onhz.server.service.auth.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
@RequiredArgsConstructor
public class SessionUpdateInterceptor implements HandlerInterceptor {

    private final SessionService sessionService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // GET요청은 세션 업데이트를 하지 않음.
        if("GET".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String deviceId = request.getHeader("Device-Id");
        if (deviceId != null) {
            sessionService.updateLastAccessedAt(deviceId);
            log.debug("세션 마지막 접근 시간 업데이트 Device-Id: {}", deviceId);
        }
        return true;
    }
}
