package com.example.fingerprint_backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ApplyDto {
    private String studentNum;
    private Boolean sign;
    private String credential;
}
