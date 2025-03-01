package com.onhz.server.exception.example;

public class BadRequestException extends BusinessException{
    public BadRequestException(ErrorCode errorCode, String description) {
        super(errorCode, description);
    }
}
