package com.example.fingerprint_backend.service.Member;

import com.example.fingerprint_backend.types.MemberRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberValidator {

    private final MemberQueryService memberQueryService;

    /**
     * 해당 학번의 멤버가 해당 권한을 가지고 있는지 확인하는 함수
     *
     * @param studentNumber - 학번
     * @param role          - 확인할 권한
     * @throws IllegalArgumentException - 해당 권한이 없을 경우
     */
    public void validateMemberInRole(String studentNumber, MemberRole role) {
        List<MemberRole> roles = memberQueryService.getMemberByStudentNumber(studentNumber).getRoles();
        if (!roles.contains(role)) {
            throw new IllegalArgumentException("해당 권한이 없습니다.");
        }
    }
}
