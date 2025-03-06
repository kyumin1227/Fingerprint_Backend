package com.example.fingerprint_backend.exception;

/**
 * 회원가입 되지 않은 사용자 예외
 */
public class UserNotRegisteredException extends RuntimeException {
    public UserNotRegisteredException(String message) {
        super(message);
    }
}
