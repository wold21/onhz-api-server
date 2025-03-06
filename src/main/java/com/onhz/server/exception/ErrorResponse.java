package com.onhz.server.exception;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ErrorResponse {
    private final String message;
    private final HttpStatus status;
    private final int statusCode;

    private ErrorResponse(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
        this.statusCode = status.value();
    }

    public static ErrorResponse of(BaseException e) {
        return new ErrorResponse(
                e.getDescription(),
                e.getErrorCode().getHttpStatus()
        );
    }

    public static ErrorResponse of(String message, HttpStatus status) {
        return new ErrorResponse(message, status);
    }
}
