package com.example.fingerprint_backend.service;

import com.example.fingerprint_backend.entity.MemberEntity;
import com.example.fingerprint_backend.repository.MemberRepository;
import com.example.fingerprint_backend.types.MemberRole;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final GetService getService;

    /**
     * 권한을 넘겨주는 함수
     */
    @Transactional
    public void changeRole(String requesterStudentNumber, String targetStudentNumber, MemberRole memberRole) {

        MemberEntity requester = getService.getMemberByStudentNumber(requesterStudentNumber);
        MemberEntity target = getService.getMemberByStudentNumber(targetStudentNumber);

        requester.removeRole(memberRole);
        target.addRole(memberRole);
    }

    /**
     * 해당 학번의 Role을 반환하는 함수
     */
    public List<MemberRole> getRole(String stdNum) {
        return getService.getMemberByStudentNumber(stdNum).getRole();
    }
}
