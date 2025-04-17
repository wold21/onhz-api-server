package com.onhz.server.dto.response.common;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommonResponse {
    private final String message;
    private final Boolean success;

    public static CommonResponse of(String message, Boolean success) {
        return CommonResponse.builder()
                .message(message)
                .success(success)
                .build();
    }
}
