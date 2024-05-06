package com.example.fingerprint_backend.dto;

import lombok.Getter;

@Getter
public class CreateFingerPrintDto {

    private String fingerprint1;
    private String fingerprint2;
    private String std_num;
    private String salt;
}
