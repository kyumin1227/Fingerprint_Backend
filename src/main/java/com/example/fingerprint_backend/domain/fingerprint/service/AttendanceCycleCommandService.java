package com.example.fingerprint_backend.domain.fingerprint.service;

import com.example.fingerprint_backend.domain.fingerprint.entity.AttendanceCycle;
import com.example.fingerprint_backend.domain.fingerprint.exception.AttendanceCycleException;
import com.example.fingerprint_backend.domain.fingerprint.repository.AttendanceCycleRepository;
import com.example.fingerprint_backend.entity.ClassClosingTime;
import com.example.fingerprint_backend.entity.MemberEntity;
import com.example.fingerprint_backend.service.LogService;
import com.example.fingerprint_backend.service.Member.MemberQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class AttendanceCycleCommandService {

    private final MemberQueryService memberQueryService;
    private final AttendanceCycleRepository attendanceCycleRepository;
    private final AttendanceCycleQueryService attendanceCycleQueryService;
    private final LogService logService;

    public AttendanceCycleCommandService(MemberQueryService memberQueryService, AttendanceCycleRepository attendanceCycleRepository, AttendanceCycleQueryService attendanceCycleQueryService, LogService logService) {
        this.memberQueryService = memberQueryService;
        this.attendanceCycleRepository = attendanceCycleRepository;
        this.attendanceCycleQueryService = attendanceCycleQueryService;
        this.logService = logService;
    }

    /**
     * 등교시 학생 번호와 시간을 입력받아 출석 주기를 생성합니다.
     *
     * @param studentNumber 학생 번호
     * @param attendTime    출석 시간
     */
    public void create(String studentNumber, LocalDateTime attendTime) {
        memberQueryService.getMemberByStudentNumber(studentNumber);

//        만약 이전에 하교를 하지 않았다면, 해당 출석 주기의 하교 시간을 출석 시간으로 설정
//        즉 체류 시간을 0으로 설정합니다.
        AttendanceCycle latestOpenCycle = attendanceCycleQueryService.getLatestOpenCycle(studentNumber);
        if (latestOpenCycle != null) {
            latestOpenCycle.setLeaveTime(latestOpenCycle.getAttendTime());
        }

//        출석 주기를 생성합니다.
        AttendanceCycle attendanceCycle = new AttendanceCycle(studentNumber, attendTime);
        attendanceCycleRepository.save(attendanceCycle);
    }

    /**
     * 하교시 학생 번호와 시간을 입력받아 출석 주기를 업데이트합니다.
     *
     * @param studentNumber 학생 번호
     * @param leaveTime     하교 시간
     * @throws AttendanceCycleException 하교 처리할 등교 기록이 없을 경우
     */
    public void close(String studentNumber, LocalDateTime leaveTime) {
        MemberEntity member = memberQueryService.getMemberByStudentNumber(studentNumber);

        AttendanceCycle latestOpenCycle = attendanceCycleQueryService.getLatestOpenCycle(studentNumber);
        if (latestOpenCycle == null) {
            throw new AttendanceCycleException("하교 처리할 등교 기록이 없습니다.");
        }

//        등교와 하교 사이에 문이 닫힌 시간이 존재하는지 확인
        try {
            ClassClosingTime classClosingTime = logService.getClassClosingTimeByTimeAfter(member.getSchoolClass().getId(), latestOpenCycle.getAttendTime());

            if (leaveTime.isAfter(classClosingTime.getClosingTime().plusMinutes(10))) {
                throw new AttendanceCycleException("하교 처리할 등교 기록이 없습니다.");
            }

        } catch (IllegalArgumentException ignored) {

        }

        latestOpenCycle.setLeaveTime(leaveTime);
    }
}
