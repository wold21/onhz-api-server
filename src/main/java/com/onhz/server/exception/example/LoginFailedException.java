package com.onhz.server.exception.example;

public class LoginFailedException extends BusinessException {
    public LoginFailedException(ErrorCode errorCode, String description) {
        super(errorCode, description);
    }
}