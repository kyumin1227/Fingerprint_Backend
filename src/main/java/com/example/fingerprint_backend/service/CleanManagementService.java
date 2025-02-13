package com.example.fingerprint_backend.service;

import com.example.fingerprint_backend.entity.Classroom;
import com.example.fingerprint_backend.entity.CleanMember;
import com.example.fingerprint_backend.repository.ClassroomRepository;
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

    @Autowired
    public CleanManagementService(ClassroomRepository classroomRepository, CleanMemberRepository cleanMemberRepository) {
        this.classroomRepository = classroomRepository;
        this.cleanMemberRepository = cleanMemberRepository;
    }

    public Classroom createClassroom(String classroomName) {
        boolean isExist = classroomRepository.existsClassroomByName(classroomName);
        if (isExist) {
            throw new IllegalStateException("이미 존재하는 반 이름입니다.");
        }
        return classroomRepository.save(new Classroom(classroomName));
    }

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
        return classroomRepository.findByName(classroomName).orElseThrow(() -> new IllegalStateException("존재하지 않는 반 이름입니다."));
    }

    /**
     * 반 이름으로 학생들을 가져온다.
     */
    public List<CleanMember> getMembersByClassroomName(String classroomName) {
        Classroom classroom = getClassroomByName(classroomName);
        return classroom.getMembers();
    }

//    public List<CleanMember> getMembersByClassroomNameAndCleanMemberStatus(String classroomName, String status) {
//        return null;
//    }
}
