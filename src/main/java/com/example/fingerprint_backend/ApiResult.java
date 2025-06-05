package com.example.fingerprint_backend;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class ApiResult {
    private boolean success;
    private String message;
    private Object data;
}
