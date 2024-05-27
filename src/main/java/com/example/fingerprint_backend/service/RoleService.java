package com.example.fingerprint_backend.service;

import com.example.fingerprint_backend.entity.MemberEntity;
import com.example.fingerprint_backend.repository.MemberRepository;
import com.example.fingerprint_backend.types.MemberRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final MemberRepository memberRepository;

    /**
     * RoleCode를 검증하여 해당 코드에 맞는 Role을 반환
     * @param roleCode
     * @return MemberRole (해당 코드에 맞는 Role)
     */
    public MemberRole checkRoleCode(String roleCode) {

        return switch (roleCode) {
            case "0" -> MemberRole.Admin;
            case "1" -> MemberRole.Professor;
            case "2" -> MemberRole.Assistant;
            case "3" -> MemberRole.Key;
            case "4" -> MemberRole.Student;
            default -> MemberRole.None;
        };

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

}
