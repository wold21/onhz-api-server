package com.onhz.server.dto.request;

import lombok.Getter;

@Getter
public class TokenRefreshRequest {
    private String refreshToken;
    private String deviceId;
}
