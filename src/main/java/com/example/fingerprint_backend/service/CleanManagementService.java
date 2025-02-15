package com.example.fingerprint_backend.service;

import com.example.fingerprint_backend.domain.CleanMembers;
import com.example.fingerprint_backend.entity.SchoolClass;
import com.example.fingerprint_backend.entity.CleanArea;
import com.example.fingerprint_backend.entity.CleanMember;
import com.example.fingerprint_backend.repository.SchoolClassRepository;
import com.example.fingerprint_backend.repository.CleanAreaRepository;
import com.example.fingerprint_backend.repository.CleanMemberRepository;
import com.example.fingerprint_backend.types.CleanAttendanceStatus;
import com.example.fingerprint_backend.types.CleanRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CleanManagementService {

    private final SchoolClassRepository schoolClassRepository;
    private final CleanMemberRepository cleanMemberRepository;
    private final CleanAreaRepository cleanAreaRepository;

    @Autowired
    public CleanManagementService(SchoolClassRepository schoolClassRepository, CleanMemberRepository cleanMemberRepository, CleanAreaRepository cleanAreaRepository) {
        this.schoolClassRepository = schoolClassRepository;
        this.cleanMemberRepository = cleanMemberRepository;
        this.cleanAreaRepository = cleanAreaRepository;
    }

    /**
     * 반 이름으로 반을 생성하는 메소드
     */
    public SchoolClass createClassroom(String classroomName) {
        boolean isExist = schoolClassRepository.existsSchoolClassByName(classroomName);
        if (isExist) {
            throw new IllegalStateException("이미 존재하는 반 이름입니다.");
        }
        return schoolClassRepository.save(new SchoolClass(classroomName));
    }

    /**
     * 학생을 생성하는 메소드 (기본값으로 ATTENDING, MEMBER 설정)
     */
    public CleanMember createMember(String studentNumber, String name, String classroomName) {
        boolean isExist = cleanMemberRepository.existsCleanMemberByStudentNumber(studentNumber);
        if (isExist) {
            throw new IllegalStateException("이미 존재하는 학번입니다.");
        }
        SchoolClass schoolClass = this.getClassroomByName(classroomName);
        CleanMember member = new CleanMember(studentNumber, name, schoolClass, CleanAttendanceStatus.ATTENDING, CleanRole.MEMBER);
        return cleanMemberRepository.save(member);
    }

    /**
     * 학생을 생성하는 메소드
     */
    public CleanMember createMember(String studentNumber, String name, String classroomName, CleanAttendanceStatus cleanAttendanceStatus, CleanRole cleanRole) {
        boolean isExist = cleanMemberRepository.existsCleanMemberByStudentNumber(studentNumber);
        if (isExist) {
            throw new IllegalStateException("이미 존재하는 학번입니다.");
        }
        SchoolClass schoolClass = this.getClassroomByName(classroomName);
        CleanMember member = new CleanMember(studentNumber, name, schoolClass, cleanAttendanceStatus, cleanRole);
        return cleanMemberRepository.save(member);
    }

    public SchoolClass getClassroomByName(String classroomName) {
        return schoolClassRepository.findByName(classroomName).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 반 이름입니다."));
    }

    /**
     * 반 이름으로 학생들을 가져온다.
     */
    public List<CleanMember> getMembersByClassroomName(String classroomName) {
        SchoolClass schoolClass = getClassroomByName(classroomName);
        return schoolClass.getMembers();
    }

    /**
     * 청소 구역을 생성하는 메소드
     */
    public CleanArea createArea(String areaName) {
        boolean isExist = cleanAreaRepository.existsCleanAreaByName(areaName);
        if (isExist) {
            throw new IllegalStateException("이미 존재하는 청소 구역 이름입니다.");
        }
        return cleanAreaRepository.save(new CleanArea(areaName));
    }

    /**
     * 학번으로 학생을 가져온다.
     */
    public CleanMember getMemberByStudentNumber(String studentNumber) {
        return cleanMemberRepository.getCleanMemberByStudentNumber(studentNumber).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 학번입니다."));
    }

    /**
     * 학생의 반을 변경하는 메소드
     */
    public CleanMember changeClassroom(String studentNumber, String classroomName) {
        CleanMember cleanMember = getMemberByStudentNumber(studentNumber);
        SchoolClass schoolClass = getClassroomByName(classroomName);
        cleanMember.setSchoolClass(schoolClass);
        return cleanMemberRepository.save(cleanMember);
    }

    /**
     * 청소 구역을 설정하는 메소드
     */
    public void setCleanArea(String classroomName, String areaName) {
        SchoolClass schoolClass = getClassroomByName(classroomName);
        CleanArea area = cleanAreaRepository.getByName(areaName).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 청소 구역 이름입니다."));
        schoolClass.appendArea(area);
        schoolClassRepository.save(schoolClass);
    }

    /**
     * 청소 구역 이름으로 청소 구역을 가져 오는 메소드
     */
    public CleanArea getAreaByName(String areaName) {
        return cleanAreaRepository.getByName(areaName).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 청소 구역 이름입니다."));
    }

    /**
     * 반 이름과 상태로 특정 상태의 학생들을 가져오는 메소드
     */
    public List<CleanMember> getMembersByClassroomNameAndCleanMemberStatus(String classroomName, CleanAttendanceStatus status) {
        SchoolClass schoolClass = getClassroomByName(classroomName);
        CleanMembers members = new CleanMembers(schoolClass.getMembers());
        return members.getMembersByStatus(status);
    }

}
