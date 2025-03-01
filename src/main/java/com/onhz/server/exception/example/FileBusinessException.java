package com.onhz.server.exception.example;

public class FileBusinessException extends BusinessException{
    public FileBusinessException(ErrorCode errorCode, String description) {
        super(errorCode, description);
    }
}