package com.onhz.server.exception;

public class TooManyRequestException extends BaseException {
    public TooManyRequestException(ErrorCode errorCode, String description) {
        super(errorCode, description);
    }
}
