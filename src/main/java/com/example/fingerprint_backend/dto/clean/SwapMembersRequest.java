package com.example.fingerprint_backend.dto.clean;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SwapMembersRequest {

    private String originStudentNumber;
    private String targetStudentNumber;
    private Long originGroupId;
    private Long targetGroupId;
}
