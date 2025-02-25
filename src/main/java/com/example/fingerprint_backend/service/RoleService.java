package com.example.fingerprint_backend.service;

import com.example.fingerprint_backend.entity.MemberEntity;
import com.example.fingerprint_backend.repository.MemberRepository;
import com.example.fingerprint_backend.types.MemberRole;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final MemberRepository memberRepository;

    @Value("${ROLE_PROFESSOR}")
    private String professorKey;
    @Value("${ROLE_ASSISTANT}")
    private String assistantKey;
    @Value("${ROLE_KEY}")
    private String keyKey;
    @Value("${ROLE_STUDENT}")
    private String studentKey;

    /**
     * RoleCode를 검증하여 해당 코드에 맞는 Role을 반환
     * @param roleCode
     * @return MemberRole (해당 코드에 맞는 Role)
     */
    public MemberRole checkRoleCode(String roleCode) {

        if (professorKey.equals(roleCode)) {
            return MemberRole.Professor;
        } else if (assistantKey.equals(roleCode)) {
            return MemberRole.Assistant;
        } else if (keyKey.equals(roleCode)) {
            return MemberRole.Key;
        } else if (studentKey.equals(roleCode)) {
            return MemberRole.Student;
        } else {
            return MemberRole.None;
        }

    }

    /**
     * 해당 멤버의 role을 변경하는 함수
     * @param stdNum
     * @param memberRole
     * @return MemberEntity
     */
    public MemberEntity changeRole(String stdNum, MemberRole memberRole) {

        Optional<MemberEntity> byStudentNumber = memberRepository.findByStudentNumber(stdNum);

        if (byStudentNumber.isEmpty()) {
            return null;
        }

        byStudentNumber.get().setRole(memberRole);

        MemberEntity changeRoleMember = memberRepository.save(byStudentNumber.get());

        return changeRoleMember;
    }

    /**
     * 해당 학번의 Role을 반환하는 함수
     * @param stdNum
     * @return MemberRole
     */
    public MemberRole getRole(String stdNum) {
        Optional<MemberEntity> byStudentNumber = memberRepository.findByStudentNumber(stdNum);
        if (byStudentNumber.isEmpty()) {
            return null;
        }
        return byStudentNumber.get().getRole();
    }

}
