package com.example.fingerprint_backend.service;

import com.example.fingerprint_backend.domain.CleanMembers;
import com.example.fingerprint_backend.entity.SchoolClass;
import com.example.fingerprint_backend.entity.CleanArea;
import com.example.fingerprint_backend.entity.CleanMember;
import com.example.fingerprint_backend.repository.SchoolClassRepository;
import com.example.fingerprint_backend.repository.CleanAreaRepository;
import com.example.fingerprint_backend.repository.CleanMemberRepository;
import com.example.fingerprint_backend.types.CleanRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class CleanManagementService {

    private final SchoolClassRepository schoolClassRepository;
    private final CleanMemberRepository cleanMemberRepository;
    private final CleanAreaRepository cleanAreaRepository;
    private final CleanHelperService cleanHelperService;

    @Autowired
    public CleanManagementService(SchoolClassRepository schoolClassRepository, CleanMemberRepository cleanMemberRepository, CleanAreaRepository cleanAreaRepository, CleanHelperService cleanHelperService) {
        this.schoolClassRepository = schoolClassRepository;
        this.cleanMemberRepository = cleanMemberRepository;
        this.cleanAreaRepository = cleanAreaRepository;
        this.cleanHelperService = cleanHelperService;
    }

    /**
     * 반 이름으로 반을 생성하는 메소드
     */
    public SchoolClass createSchoolClass(String schoolClassName) {
        cleanHelperService.validateSchoolClassNameIsUnique(schoolClassName);
        return schoolClassRepository.save(new SchoolClass(schoolClassName));
    }

    /**
     * 학생을 생성하는 메소드 (기본값으로 MEMBER 설정)
     */
    public CleanMember createMember(String studentNumber, String name, String schoolClassName) {
        cleanHelperService.validateStudentNumberIsUnique(studentNumber);
        SchoolClass schoolClass = cleanHelperService.getSchoolClassByName(schoolClassName);
        CleanMember member = new CleanMember(studentNumber, name, schoolClass);
        member.setCleanArea(schoolClass.getDefaultArea());
        CleanMember save = cleanMemberRepository.save(member);
        schoolClass.appendMember(save);
        return save;
    }

    /**
     * 학생을 생성하는 메소드
     */
    public CleanMember createMember(String studentNumber, String name, String schoolClassName, CleanRole cleanRole) {
        cleanHelperService.validateStudentNumberIsUnique(studentNumber);
        SchoolClass schoolClass = cleanHelperService.getSchoolClassByName(schoolClassName);
        CleanMember member = new CleanMember(studentNumber, name, schoolClass, cleanRole);
        member.setCleanArea(schoolClass.getDefaultArea());
        CleanMember save = cleanMemberRepository.save(member);
        schoolClass.appendMember(save);
//        if (cleanRole == CleanRole.MANAGER) {
//            schoolClass.setManager(save);
//        }
        return save;
    }

//    /**
//     * 반 이름으로 학생들을 가져온다.
//     */
//    public Set<CleanMember> getMembersBySchoolClassName(String schoolClassName) {
//        SchoolClass schoolClass = getSchoolClassByName(schoolClassName);
//        return schoolClass.getMembers();
//    }
//
//    /**
//     * 청소 구역을 생성하는 메소드
//     */
//    public CleanArea createArea(String areaName) {
//        // TODO : 청소 구역 생성 시, 같은 반에 중복되는 청소 구역이 없도록 확인
//        boolean isExist = cleanAreaRepository.existsCleanAreaByName(areaName);
//        if (isExist) {
//            throw new IllegalStateException("이미 존재하는 청소 구역 이름입니다.");
//        }
//        return cleanAreaRepository.save(new CleanArea(areaName));
//    }
//
//    /**
//     * 학번으로 학생을 가져온다.
//     */
//    public CleanMember getMemberByStudentNumber(String studentNumber) {
//        return cleanMemberRepository.getCleanMemberByStudentNumber(studentNumber).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 학번입니다."));
//    }
//
//    /**
//     * 청소 구역을 설정하는 메소드
//     */
//    public void setCleanArea(String schoolClassName, String areaName) {
//        SchoolClass schoolClass = getSchoolClassByName(schoolClassName);
//        CleanArea area = cleanAreaRepository.getByName(areaName).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 청소 구역 이름입니다."));
//        schoolClass.appendArea(area);
//        schoolClassRepository.save(schoolClass);
//    }
//
//    /**
//     * 반 이름과 상태로 특정 상태의 학생들을 가져오는 메소드
//     */
//    public List<CleanMember> getMembersBySchoolClassNameAndCleanMemberStatus(String schoolClassName, CleanArea area) {
//        SchoolClass schoolClass = getSchoolClassByName(schoolClassName);
//        CleanMembers members = new CleanMembers(schoolClass.getMembers().stream().toList());
//        return members.getMembersByArea(area);
//    }

}
