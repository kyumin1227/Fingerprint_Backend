package com.example.fingerprint_backend.dto.clean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberRequest {
    private String studentNumber;
    private String givenName;
    private String familyName;
    private String className;
    private String areaName;

}
