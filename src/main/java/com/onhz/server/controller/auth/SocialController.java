package com.onhz.server.controller.auth;

import com.onhz.server.dto.response.ApiResponse;
import com.onhz.server.service.auth.SocialService;
import com.onhz.server.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/api/v1/auth/social/unlink")
@RequiredArgsConstructor
public class SocialController {
    private final SocialService socialService;

    @PostMapping("/kakao")
    public ApiResponse<?> unlinkKakao (
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("user_id") String userId,
            @RequestParam(value = "referrer_type", required = false) String referrerType) {
        socialService.unlinkSocial(userId);
        return ApiResponse.success(HttpStatus.OK, "success", null);
    }

    @PostMapping("/naver")
    public ApiResponse<?> unlinkNaver (
            @RequestHeader("signature") String signature,
            @RequestBody Map<String, String> payload) {
        String clientId = payload.get("client_id");
        String userId = payload.get("user_id");
        String expires = payload.get("expires");
        String timestamp = payload.get("timestamp");

        boolean isValid = socialService.verifyNaverSignature(signature, clientId, userId, expires, timestamp);
        if (!isValid) {
            throw new IllegalArgumentException("서명 실패");
        }
        socialService.unlinkSocial(userId);
        return ApiResponse.success(HttpStatus.OK, "success", null);
    }
}
