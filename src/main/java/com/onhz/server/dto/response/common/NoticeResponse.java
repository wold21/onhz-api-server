package com.onhz.server.dto.response.common;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NoticeResponse {
    private final String message;

    public static NoticeResponse of(String message) {
        return NoticeResponse.builder()
                .message(message)
                .build();
    }
}
