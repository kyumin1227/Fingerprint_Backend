package com.example.fingerprint_backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class KakaoEntity {
    @Id
    private String studentNum;

    private Boolean kakaoAgree;
    private String uuid;
    private String accessToken;
    private String refreshToken;

}
