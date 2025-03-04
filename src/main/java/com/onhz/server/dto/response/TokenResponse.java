package com.onhz.server.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenResponse {
    private String accessToken;
    private String refreshToken;
    private String deviceId;
    @Builder.Default
    private String tokenType = "Bearer";
}