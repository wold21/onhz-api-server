package com.onhz.server.exception;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ErrorResponse {
    private String massage;
    private HttpStatus statusName;
    private int statusCode;

    public ErrorResponse(String massage, HttpStatus statusName, int statusCode) {
        this.massage = massage;
        this.statusName = statusName;
        this.statusCode = statusCode;

    }
}
