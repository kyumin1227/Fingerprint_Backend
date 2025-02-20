package com.example.fingerprint_backend.dto.clean;

import com.example.fingerprint_backend.types.CleanRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberRequest {
    private String studentNumber;
    private String firstName;
    private String givenName;
    private String className;
    private String areaName;
    private CleanRole role;

    public void setRole(String role) {
        try {
            this.role = (role == null) ?
                    CleanRole.MEMBER : CleanRole.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("존재하지 않는 역할 입니다.");
        }
    }
}
