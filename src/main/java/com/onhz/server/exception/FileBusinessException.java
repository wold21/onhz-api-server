package com.onhz.server.exception;

public class FileBusinessException extends BaseException {
    public FileBusinessException(ErrorCode errorCode, String description) {
        super(errorCode, description);
    }
}
