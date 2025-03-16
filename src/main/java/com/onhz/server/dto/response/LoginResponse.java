package com.onhz.server.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {
    private final String accessToken;
    private final String refreshToken;
    private final String deviceId;
    private final UserResponse user;
    @Builder.Default
    private String tokenType = "Bearer";
}