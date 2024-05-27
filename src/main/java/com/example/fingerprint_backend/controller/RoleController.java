package com.example.fingerprint_backend.controller;

import com.example.fingerprint_backend.ApiResponse;
import com.example.fingerprint_backend.dto.RoleChangeDto;
import com.example.fingerprint_backend.entity.MemberEntity;
import com.example.fingerprint_backend.service.GoogleService;
import com.example.fingerprint_backend.service.RoleService;
import com.example.fingerprint_backend.types.MemberRole;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
@RequiredArgsConstructor
public class RoleController {

    private final GoogleService googleService;
    private final RoleService roleService;

    @PostMapping("/api/role")
    public ResponseEntity<ApiResponse> roleChange(@RequestBody RoleChangeDto roleChangeDto) throws GeneralSecurityException, IOException {

        System.out.println("roleChangeDto.getCredential() = " + roleChangeDto.getCredential());

        if (!googleService.googleTokenCheck(roleChangeDto.getCredential())) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(false, "올바르지 않은 토큰입니다.", null));
        };

        MemberRole memberRole = roleService.checkRoleCode(roleChangeDto.getRoleCode());

        if (memberRole == MemberRole.None) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(false, "올바르지 않은 코드입니다.", null));
        }

        MemberEntity changedRoleMember = roleService.changeRole(roleChangeDto.getStudentNum(), memberRole);

        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "권한 변경 성공: " + roleChangeDto.getRole() + " -> " + memberRole, changedRoleMember));
    }
}
