package com.onhz.server.exception;

public class SocialDisconnectionException extends BaseException {
    public SocialDisconnectionException(ErrorCode errorCode, String description) {
        super(errorCode, description);
    }
}
