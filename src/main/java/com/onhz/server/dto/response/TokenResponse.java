package com.onhz.server.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenResponse {
    private final String accessToken;
    private final String refreshToken;
    private final String deviceId;
    @Builder.Default
    private String tokenType = "Bearer";
}