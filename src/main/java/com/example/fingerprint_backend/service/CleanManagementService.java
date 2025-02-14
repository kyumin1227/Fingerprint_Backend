package com.example.fingerprint_backend.service;

import com.example.fingerprint_backend.domain.CleanMembers;
import com.example.fingerprint_backend.entity.Classroom;
import com.example.fingerprint_backend.entity.CleanArea;
import com.example.fingerprint_backend.entity.CleanMember;
import com.example.fingerprint_backend.repository.ClassroomRepository;
import com.example.fingerprint_backend.repository.CleanAreaRepository;
import com.example.fingerprint_backend.repository.CleanMemberRepository;
import com.example.fingerprint_backend.types.CleanAttendanceStatus;
import com.example.fingerprint_backend.types.CleanRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CleanManagementService {

    private final ClassroomRepository classroomRepository;
    private final CleanMemberRepository cleanMemberRepository;
    private final CleanAreaRepository cleanAreaRepository;

    @Autowired
    public CleanManagementService(ClassroomRepository classroomRepository, CleanMemberRepository cleanMemberRepository, CleanAreaRepository cleanAreaRepository) {
        this.classroomRepository = classroomRepository;
        this.cleanMemberRepository = cleanMemberRepository;
        this.cleanAreaRepository = cleanAreaRepository;
    }

    /**
     * 반 이름으로 반을 생성하는 메소드
     */
    public Classroom createClassroom(String classroomName) {
        boolean isExist = classroomRepository.existsClassroomByName(classroomName);
        if (isExist) {
            throw new IllegalStateException("이미 존재하는 반 이름입니다.");
        }
        return classroomRepository.save(new Classroom(classroomName));
    }

    /**
     * 학생을 생성하는 메소드 (기본값으로 ATTENDING, MEMBER 설정)
     */
    public CleanMember createMember(String studentNumber, String name, String classroomName) {
        boolean isExist = cleanMemberRepository.existsCleanMemberByStudentNumber(studentNumber);
        if (isExist) {
            throw new IllegalStateException("이미 존재하는 학번입니다.");
        }
        Classroom classroom = this.getClassroomByName(classroomName);
        CleanMember member = new CleanMember(studentNumber, name, classroom, CleanAttendanceStatus.ATTENDING, CleanRole.MEMBER);
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
        Classroom classroom = this.getClassroomByName(classroomName);
        CleanMember member = new CleanMember(studentNumber, name, classroom, cleanAttendanceStatus, cleanRole);
        return cleanMemberRepository.save(member);
    }

    public Classroom getClassroomByName(String classroomName) {
        return classroomRepository.findByName(classroomName).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 반 이름입니다."));
    }

    /**
     * 반 이름으로 학생들을 가져온다.
     */
    public List<CleanMember> getMembersByClassroomName(String classroomName) {
        Classroom classroom = getClassroomByName(classroomName);
        return classroom.getMembers();
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
        Classroom classroom = getClassroomByName(classroomName);
        cleanMember.setClassroom(classroom);
        return cleanMemberRepository.save(cleanMember);
    }

    /**
     * 청소 구역을 설정하는 메소드
     */
    public void setCleanArea(String classroomName, String areaName) {
        Classroom classroom = getClassroomByName(classroomName);
        CleanArea area = cleanAreaRepository.getByName(areaName).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 청소 구역 이름입니다."));
        classroom.appendArea(area);
        classroomRepository.save(classroom);
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
        Classroom classroom = getClassroomByName(classroomName);
        CleanMembers members = new CleanMembers(classroom.getMembers());
        return members.getMembersByStatus(status);
    }

}
