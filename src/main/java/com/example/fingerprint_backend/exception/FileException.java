package com.example.fingerprint_backend.exception;

/**
 * 파일 관련 예외
 */
public class FileException extends RuntimeException {
    public FileException(String message) {
        super(message);
    }
}
