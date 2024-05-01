package com.example.fingerprint_backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GoogleRegisterDto {

    private String credential;
    private String email;
    private String name;
    private String kakao;
    private String studentNum;

}
