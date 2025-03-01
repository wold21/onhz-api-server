package com.onhz.server.exception.example;

public class LogoutFailedException extends BusinessException {
    public LogoutFailedException(ErrorCode errorCode, String description) {
        super(errorCode, description);
    }
}