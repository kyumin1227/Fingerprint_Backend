package com.example.fingerprint_backend.domain.fingerprint.exception;

/**
 * 출석체크 주기 설정 시 예외
 */
public class CycleException extends RuntimeException {
    public CycleException(String message) {
        super(message);
    }
}
