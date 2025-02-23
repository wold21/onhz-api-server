package com.onhz.server.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenResponseDto {
    private String accessToken;
    private String refreshToken;
    private String deviceId;
    @Builder.Default
    private String tokenType = "Bearer";
}