package com.onhz.server.exception.example;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder
public class ErrorResponse {

    private String messages;
    private int status;

    static final ErrorResponse of(String messages, HttpStatus httpStatus) {
        return ErrorResponse.builder()
                .messages(messages)
                .status(httpStatus.value())
                .build();
    }
}
