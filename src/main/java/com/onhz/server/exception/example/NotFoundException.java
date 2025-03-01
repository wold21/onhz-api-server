package com.onhz.server.exception.example;

public class NotFoundException extends BusinessException{
    public NotFoundException(ErrorCode errorCode, String description) {
        super(errorCode, description);
    }
}
