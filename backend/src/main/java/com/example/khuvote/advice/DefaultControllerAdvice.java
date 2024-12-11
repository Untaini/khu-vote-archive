package com.example.khuvote.advice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class DefaultControllerAdvice {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> exceptionResponse(RuntimeException e) {
        return ResponseEntity.badRequest()
                .body(new DefaultExceptionResponse(e.getMessage()));
    }

    public record DefaultExceptionResponse(
            String errorMessage
    ) {}
}
