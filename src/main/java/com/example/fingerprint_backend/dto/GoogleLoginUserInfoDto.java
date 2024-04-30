package com.example.fingerprint_backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GoogleLoginUserInfoDto {
    private String email;
    private String name;
}
