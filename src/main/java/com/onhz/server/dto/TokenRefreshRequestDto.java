package com.onhz.server.dto;

import lombok.Getter;

@Getter
public class TokenRefreshRequestDto {
    private String refreshToken;
    private String deviceId;
}
