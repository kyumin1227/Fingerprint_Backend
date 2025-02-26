package com.example.fingerprint_backend.dto;

import com.example.fingerprint_backend.entity.MemberEntity;
import com.example.fingerprint_backend.entity.SchoolClass;
import com.example.fingerprint_backend.types.MemberLanguage;
import com.example.fingerprint_backend.types.MemberRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Getter @Setter
public class LoginResponse {

    private String studentNumber;
    private String email;
    private String name;
    private String givenName;
    private String familyName;
    private MemberLanguage language;
    private List<MemberRole> role;
    private String className;
    private String profileImage;
    private LocalDateTime registerTime;
    private String token;

    public LoginResponse(MemberEntity memberEntity, String accessToken) {
        this.studentNumber = memberEntity.getStudentNumber();
        this.email = memberEntity.getEmail();
        this.name = memberEntity.getName();
        this.givenName = memberEntity.getGivenName();
        this.familyName = memberEntity.getFamilyName();
        this.language = memberEntity.getLanguage();
        this.role = memberEntity.getRole();
        this.className = memberEntity.getSchoolClass().getName();
        this.profileImage = memberEntity.getProfileImage();
        this.registerTime = LocalDateTime.now();
        this.token = accessToken;
    }

}
