package com.example.fingerprint_backend.controller;

import com.example.fingerprint_backend.ApiResult;
import com.example.fingerprint_backend.exception.FileException;
import com.example.fingerprint_backend.exception.UserNotRegisteredException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class ErrorController {

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;

    /**
     * 올바르지 않은 값을 전달했을 때 예외 처리
     */
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<ApiResult> illegalArgumentException(Exception e) {
        return ResponseEntity.badRequest().body(new ApiResult(false, e.getMessage(), null));
    }

    /**
     * 인증 관련 예외 처리
     */
    @ExceptionHandler({AuthenticationCredentialsNotFoundException.class, InsufficientAuthenticationException.class, BadCredentialsException.class})
    public ResponseEntity<ApiResult> authenticationException(Exception e) {
        return ResponseEntity.status(401).body(new ApiResult(false, e.getMessage(), null));
    }

    /**
     * 회원가입 되지 않은 사용자 예외 처리
     */
    @ExceptionHandler({UserNotRegisteredException.class})
    public ResponseEntity<ApiResult> userNotRegisteredException(Exception e) {
        return ResponseEntity.status(403).body(new ApiResult(false, e.getMessage(), null));
    }

    /**
     * 파일 업로드 예외 처리
     */
    @ExceptionHandler({FileException.class})
    public ResponseEntity<ApiResult> fileException(Exception e) {
        return ResponseEntity.status(400).body(new ApiResult(false, e.getMessage(), null));
    }

    /**
     * 파일 크기 초과 예외 처리
     */
    @ExceptionHandler({MaxUploadSizeExceededException.class})
    public ResponseEntity<ApiResult> maxUploadSizeExceededException(Exception e) {
        return ResponseEntity.status(400).body(new ApiResult(false, "파일 크기가 너무 큽니다. 최대 " + maxFileSize + "까지 업로드 가능합니다.", null));
    }

}
