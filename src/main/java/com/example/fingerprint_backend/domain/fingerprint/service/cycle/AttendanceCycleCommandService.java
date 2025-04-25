package com.example.fingerprint_backend.domain.fingerprint.service.cycle;

import com.example.fingerprint_backend.domain.fingerprint.entity.AttendanceCycle;
import com.example.fingerprint_backend.domain.fingerprint.entity.OutingCycle;
import com.example.fingerprint_backend.domain.fingerprint.event.DailyStatsUpdateEvent;
import com.example.fingerprint_backend.domain.fingerprint.exception.CycleException;
import com.example.fingerprint_backend.domain.fingerprint.repository.AttendanceCycleRepository;
import com.example.fingerprint_backend.domain.fingerprint.repository.OutingCycleRepository;
import com.example.fingerprint_backend.domain.fingerprint.service.LogService;
import com.example.fingerprint_backend.entity.MemberEntity;
import com.example.fingerprint_backend.service.Member.MemberQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class AttendanceCycleCommandService {

//    TODO - 출석 사이클과 외출 사이클을 분리

    private final MemberQueryService memberQueryService;
    private final AttendanceCycleRepository attendanceCycleRepository;
    private final AttendanceCycleQueryService attendanceCycleQueryService;
    private final LogService logService;
    private final OutingCycleRepository outingCycleRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 등교시 학생 번호와 시간을 입력받아 출석 주기를 생성합니다.
     *
     * @param studentNumber 학생 번호
     * @param attendTime    출석 시간
     */
    public AttendanceCycle createAttendanceCycle(String studentNumber, LocalDateTime attendTime) {

        // 만약 이전에 하교를 하지 않았다면, 해당 출석 주기의 하교 시간을 출석 시간으로 설정
        // 즉 체류 시간을 0으로 설정합니다.
        AttendanceCycle latestOpenCycle = attendanceCycleQueryService.getLatestOpenCycle(studentNumber);
        if (latestOpenCycle != null) {
            latestOpenCycle.setLeaveTime(latestOpenCycle.getAttendTime());
        }

        // 출석 주기를 생성합니다.
        AttendanceCycle attendanceCycle = new AttendanceCycle(studentNumber, attendTime);
        return attendanceCycleRepository.save(attendanceCycle);
    }

    /**
     * 최신의 하교하지 않은 출석 사이클을 가져옵니다. (없으면 생성합니다.)
     *
     * @param studentNumber 학번
     * @param attendTime    없는 경우 등교로 생성할 시간
     * @return AttendanceCycle
     */
    public AttendanceCycle getOrCreateLatestOpenCycle(String studentNumber, LocalDateTime attendTime) {

        AttendanceCycle openAttendCycle = attendanceCycleQueryService.getLatestOpenCycle(studentNumber);
        if (openAttendCycle == null) {
            openAttendCycle = createAttendanceCycle(studentNumber, attendTime);
        }
        return openAttendCycle;
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

        AttendanceCycle openAttendCycle = attendanceCycleQueryService.getLatestOpenCycle(studentNumber);
        if (openAttendCycle == null) {
            openAttendCycle = createAttendanceCycle(studentNumber, leaveTime);
        }

        // 등교와 하교 사이에 문이 닫힌 시간이 존재하는지 확인
//        try {
//            ClassClosingTime classClosingTime = logService
//                    .getClassClosingTimeByTimeAfter(member.getSchoolClass().getId(), openAttendCycle.getAttendTime());
//
//            if (leaveTime.isAfter(classClosingTime.getClosingTime().plusMinutes(10))) {
//                openAttendCycle = createAttendanceCycle(studentNumber, leaveTime);
//            }
//
//        } catch (LogException ignored) {
//
//        }

        openAttendCycle.setLeaveTime(leaveTime);

        eventPublisher.publishEvent(new DailyStatsUpdateEvent(openAttendCycle));

        return openAttendCycle;
    }

    /**
     * 출석 사이클에 외출 사이클을 추가합니다.
     *
     * @param attendanceCycle 출석 사이클
     * @param outingCycle     외출 사이클
     */
    public void addOutingCycle(AttendanceCycle attendanceCycle, OutingCycle outingCycle) {

        attendanceCycle.addOutingCycle(outingCycle);
    }

}
