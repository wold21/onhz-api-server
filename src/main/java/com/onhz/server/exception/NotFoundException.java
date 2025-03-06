package com.onhz.server.exception;

import com.onhz.server.exception.example.BusinessException;
import com.onhz.server.exception.example.ErrorCode;

public class NotFoundException extends BaseException {
    public NotFoundException(ErrorCode errorCode, String description) {
        super(errorCode, description);
    }
}
