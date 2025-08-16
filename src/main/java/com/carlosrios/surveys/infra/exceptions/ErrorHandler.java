package com.carlosrios.surveys.infra.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;

@RestControllerAdvice(basePackages = "com.carlosrios.surveys") // handles exceptions for the specified package
@Slf4j
public class ErrorHandler {

    @ExceptionHandler(TitleNotValidException.class)
    public ResponseEntity<ErrorResponse> handle(TitleNotValidException ex) {
        var errors = new HashMap<String, String>();
        errors.put("code", String.valueOf(HttpStatus.BAD_REQUEST.value()));
        errors.put("status", HttpStatus.BAD_REQUEST.getReasonPhrase());
        errors.put("message", ex.getMessage());
        log.warn("Title Not Valid: {}", ex.toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(errors));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    private ResponseEntity<ErrorResponse> illegalArgumentExceptionHandler(IllegalArgumentException ex){
        var errors = new HashMap<String, String>();

        errors.put("code", String.valueOf(HttpStatus.BAD_REQUEST.value()));
        errors.put("status", HttpStatus.BAD_REQUEST.getReasonPhrase());
        errors.put("message", ex.getMessage());

        return ResponseEntity.badRequest().body(new ErrorResponse(errors));
    }

}
