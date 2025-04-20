package com.example.fingerprint_backend.domain.fingerprint.service;

import com.example.fingerprint_backend.domain.fingerprint.entity.AttendanceCycle;
import com.example.fingerprint_backend.domain.fingerprint.entity.OutingCycle;
import com.example.fingerprint_backend.domain.fingerprint.exception.CycleException;
import com.example.fingerprint_backend.domain.fingerprint.exception.LogException;
import com.example.fingerprint_backend.domain.fingerprint.repository.AttendanceCycleRepository;
import com.example.fingerprint_backend.domain.fingerprint.repository.OutingCycleRepository;
import com.example.fingerprint_backend.domain.fingerprint.entity.ClassClosingTime;
import com.example.fingerprint_backend.entity.MemberEntity;
import com.example.fingerprint_backend.service.Member.MemberQueryService;
import com.example.fingerprint_backend.types.LogAction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class CycleCommandService {

    private final MemberQueryService memberQueryService;
    private final AttendanceCycleRepository attendanceCycleRepository;
    private final CycleQueryService cycleQueryService;
    private final LogService logService;
    private final OutingCycleRepository outingCycleRepository;

    public CycleCommandService(MemberQueryService memberQueryService, AttendanceCycleRepository attendanceCycleRepository, CycleQueryService cycleQueryService, LogService logService, OutingCycleRepository outingCycleRepository) {
        this.memberQueryService = memberQueryService;
        this.attendanceCycleRepository = attendanceCycleRepository;
        this.cycleQueryService = cycleQueryService;
        this.logService = logService;
        this.outingCycleRepository = outingCycleRepository;
    }

    /**
     * 등교시 학생 번호와 시간을 입력받아 출석 주기를 생성합니다.
     *
     * @param studentNumber 학생 번호
     * @param attendTime    출석 시간
     */
    public AttendanceCycle createAttendanceCycle(String studentNumber, LocalDateTime attendTime) {

//        만약 이전에 하교를 하지 않았다면, 해당 출석 주기의 하교 시간을 출석 시간으로 설정
//        즉 체류 시간을 0으로 설정합니다.
        AttendanceCycle latestOpenCycle = cycleQueryService.getLatestOpenCycle(studentNumber);
        if (latestOpenCycle != null) {
            latestOpenCycle.setLeaveTime(latestOpenCycle.getAttendTime());
        }

//        출석 주기를 생성합니다.
        AttendanceCycle attendanceCycle = new AttendanceCycle(studentNumber, attendTime);
        return attendanceCycleRepository.save(attendanceCycle);
    }

    /**
     * 하교시 학생 번호와 시간을 입력받아 출석 주기를 업데이트합니다.
     *
     * @param studentNumber 학생 번호
     * @param leaveTime     하교 시간
     * @throws CycleException 하교 처리할 등교 기록이 없을 경우
     */
    public AttendanceCycle closeAttendanceCycle(String studentNumber, LocalDateTime leaveTime) {
        MemberEntity member = memberQueryService.getMemberByStudentNumber(studentNumber);

        AttendanceCycle openAttendCycle = cycleQueryService.getLatestOpenCycle(studentNumber);
        if (openAttendCycle == null) {
            openAttendCycle = createAttendanceCycle(studentNumber, leaveTime);
        }

//        등교와 하교 사이에 문이 닫힌 시간이 존재하는지 확인
        try {
            ClassClosingTime classClosingTime = logService.getClassClosingTimeByTimeAfter(member.getSchoolClass().getId(), openAttendCycle.getAttendTime());

            if (leaveTime.isAfter(classClosingTime.getClosingTime().plusMinutes(10))) {
                openAttendCycle = createAttendanceCycle(studentNumber, leaveTime);
            }

        } catch (LogException ignored) {

        }

        openAttendCycle.setLeaveTime(leaveTime);

        return openAttendCycle;
    }

    private OutingCycle addOutingCycle(String studentNumber, OutingCycle outingCycle) {
        AttendanceCycle openCycle = cycleQueryService.getLatestOpenCycle(studentNumber);

        if (openCycle == null) {
//            열려있는 등교 사이클이 없는 경우
            openCycle = createAttendanceCycle(studentNumber, outingCycle.getOutingStartTime());
        }

        openCycle.addOutingCycle(outingCycle);

        return outingCycle;
    }

    /**
     * 외출 사이클을 생성합니다. (만약 등교 사이클이 없다면 생성합니다.)
     *
     * @param studentNumber   학번
     * @param outingStartTime 외출 시작 시간
     * @param reason          외출 사유
     * @return 외출 기록
     */
    public OutingCycle createOutingCycle(String studentNumber, LocalDateTime outingStartTime, LogAction reason) {

        OutingCycle latestOpenOutingCycle = cycleQueryService.getLatestOpenOutingCycle(studentNumber);

        if (latestOpenOutingCycle != null) {
            latestOpenOutingCycle.setOutingEndTime(latestOpenOutingCycle.getOutingStartTime());
        }

        OutingCycle outingCycle = new OutingCycle(studentNumber, outingStartTime, reason);

        OutingCycle outingCycleWithAttendCycle = addOutingCycle(studentNumber, outingCycle);

        return outingCycleRepository.save(outingCycleWithAttendCycle);
    }

    /**
     * 외출 사이클을 종료합니다. (만약 생성된 외출 사이클이 없다면 생성합니다.)
     *
     * @param studentNumber 학번
     * @param outingEndTime 외출 종료 시간
     * @return 외출 기록
     */
    public OutingCycle closeOutingCycle(String studentNumber, LocalDateTime outingEndTime) {

        OutingCycle outingCycle = cycleQueryService.getLatestOpenOutingCycle(studentNumber);

        if (outingCycle == null) {
//            열려있는 외출 사이클이 없는 경우
            outingCycle = createOutingCycle(studentNumber, outingEndTime, LogAction.기타);
        }

        outingCycle.setOutingEndTime(outingEndTime);

        return outingCycle;
    }
}
