package com.example.fingerprint_backend;

import com.example.fingerprint_backend.dto.GoogleRegisterDto;
import com.example.fingerprint_backend.dto.LoginResponse;
import com.example.fingerprint_backend.entity.MemberEntity;
import com.example.fingerprint_backend.entity.SchoolClass;
import com.example.fingerprint_backend.service.AuthService;
import com.example.fingerprint_backend.types.MemberRole;

public class TestMemberFactory {

    private static AuthService authService;

    public static LoginResponse createLoginResponse(String studentNumber, String name, MemberRole role, SchoolClass schoolClass) {
        MemberEntity memberEntity = new MemberEntity(studentNumber, name + "email", name, "givenName", "familyName", null, null);
        memberEntity.setSchoolClass(schoolClass);
        memberEntity.addRole(role);
        return new LoginResponse(memberEntity, null);
    }

    public static GoogleRegisterDto createGoogleRegisterDto(String studentNumber, String name) {
        GoogleRegisterDto googleRegisterDto = new GoogleRegisterDto();
        googleRegisterDto.setStudentNumber(studentNumber);
        googleRegisterDto.setClassName(name);
        return googleRegisterDto;
    }
}
