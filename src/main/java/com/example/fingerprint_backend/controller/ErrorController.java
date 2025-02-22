package com.example.fingerprint_backend.controller;

import com.example.fingerprint_backend.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorController {

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<ApiResponse> illegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
    }
}
