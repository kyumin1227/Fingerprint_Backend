package com.example.fingerprint_backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class KakaoEntity {
    @Id
    private String studentNumber;

    private Boolean kakaoAgree;
    private String uuid;
    private String accessToken;
    private String refreshToken;
    private String scope;
    private String profileId;
    private LocalDateTime registrationTime;

}
