package com.onhz.server.common.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CommonUtils {
    public static String generateRandomString(int length) {
        Random random = new Random();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        int start = random.nextInt(uuid.length() - length);
        String uuidPart = uuid.substring(start, start + length);
        return uuidPart;
    }

    public static String generateSecureCode() {
        final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        final int CODE_LENGTH = 6;
        Random random = new Random();
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return code.toString();
    }

    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
