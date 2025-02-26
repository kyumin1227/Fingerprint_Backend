package com.example.fingerprint_backend.service;

import com.example.fingerprint_backend.entity.MemberEntity;
import com.example.fingerprint_backend.entity.SchoolClass;
import com.example.fingerprint_backend.repository.SchoolClassRepository;
import com.example.fingerprint_backend.types.MemberRole;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final GetService getService;
    private final SchoolClassRepository schoolClassRepository;

    /**
     * 해당 멤버의 반을 설정하는 함수
     */
    @Transactional
    public void setSchoolClass(MemberEntity member, String className) {
        SchoolClass schoolClass = schoolClassRepository.findSchoolClassByName(className)
                .orElseThrow(() -> new AuthenticationCredentialsNotFoundException("존재하지 않는 반입니다."));

        if (member.getSchoolClass() != null) {
            member.getSchoolClass().removeMember(member);
        }

        member.setSchoolClass(schoolClass);
        schoolClass.appendMember(member);
    }

    /**
     * 권한을 넘겨주는 함수
     */
    @Transactional
    public void transferRole(String requesterStudentNumber, String targetStudentNumber, MemberRole memberRole) {

        MemberEntity requester = getService.getMemberByStudentNumber(requesterStudentNumber);
        MemberEntity target = getService.getMemberByStudentNumber(targetStudentNumber);

        requester.removeRole(memberRole);
        target.addRole(memberRole);
    }

    /**
     * 해당 학번의 Role을 반환하는 함수
     */
    public List<MemberRole> getRole(String studentNumber) {
        return getService.getMemberByStudentNumber(studentNumber).getRoles();
    }

    /**
     * 해당 학번의 반 ID를 반환하는 함수
     */
    public Long getClassId(String studentNumber) {
        MemberEntity member = getService.getMemberByStudentNumber(studentNumber);
        SchoolClass schoolClass = member.getSchoolClass();
        if (schoolClass == null) {
            return null;
        }
        return schoolClass.getId();
    }
}
