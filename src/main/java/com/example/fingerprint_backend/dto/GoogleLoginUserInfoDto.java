package com.example.fingerprint_backend.dto;

import com.example.fingerprint_backend.types.MemberRole;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GoogleLoginUserInfoDto {
    private String email;
    private String name;
    private String picture;
    private Long exp;
    private String studentNumber;
    private String kakao;
    private MemberRole role;
}
