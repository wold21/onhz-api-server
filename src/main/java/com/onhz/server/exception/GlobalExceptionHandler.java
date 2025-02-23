package com.onhz.server.exception;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", e.getMessage());
        response.put("statusCode", HttpStatus.BAD_REQUEST.value());
        response.put("statusName", HttpStatus.BAD_REQUEST.name());

        return ResponseEntity
                .badRequest()
                .body(response);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<Map<String, Object>> handleJwtException(ExpiredJwtException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", e.getMessage());
        response.put("statusCode", HttpStatus.UNAUTHORIZED.value());
        response.put("statusName", HttpStatus.UNAUTHORIZED.name());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", ")));
        response.put("statusCode", HttpStatus.BAD_REQUEST.value());
        response.put("statusName", HttpStatus.BAD_REQUEST.name());

        return ResponseEntity
                .badRequest()
                .body(response);
    }
}
