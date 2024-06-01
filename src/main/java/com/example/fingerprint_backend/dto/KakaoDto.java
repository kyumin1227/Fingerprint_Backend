package com.example.fingerprint_backend.dto;

import lombok.Getter;

@Getter
public class KakaoDto {
    private String redirect_uri;
    private String code;
    private String studentNumber;
}
