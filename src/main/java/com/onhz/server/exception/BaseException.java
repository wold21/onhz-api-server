package com.onhz.server.exception;

import com.onhz.server.exception.example.ErrorCode;
import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {

    private final com.onhz.server.exception.example.ErrorCode errorCode;
    private final String description;

    public BaseException(ErrorCode errorCode, String description) {
        super(description);
        this.errorCode = errorCode;
        this.description = description;
    }
}
