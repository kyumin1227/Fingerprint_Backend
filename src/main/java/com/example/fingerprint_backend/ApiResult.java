package com.example.fingerprint_backend;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ApiResult {
    private boolean success;
    private String message;
    private Object data;

    public ApiResult(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

}
