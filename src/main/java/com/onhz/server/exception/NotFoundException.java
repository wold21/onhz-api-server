package com.onhz.server.exception;

public class NotFoundException extends BaseException {
    public NotFoundException(ErrorCode errorCode, String description) {
        super(errorCode, description);
    }
}
