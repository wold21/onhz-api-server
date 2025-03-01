package com.onhz.server.exception.example;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    BAD_REQUEST_EXCEPTION(HttpStatus.BAD_REQUEST),
    NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND),
    FILE_BUSINESS_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED),
    LOGOUT_FAILED(HttpStatus.UNAUTHORIZED);

    private final HttpStatus httpStatus;
}