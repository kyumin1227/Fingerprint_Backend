package com.example.fingerprint_backend.dto;

import com.example.fingerprint_backend.types.MemberRole;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RoleChangeDto {
    String credential;
    String studentNum;
    MemberRole role;
    String roleCode;
}
