package com.example.fingerprint_backend.domain.fingerprint.service.cycle;

import com.example.fingerprint_backend.domain.fingerprint.entity.AttendanceCycle;
import com.example.fingerprint_backend.domain.fingerprint.entity.ClassClosingTime;
import com.example.fingerprint_backend.domain.fingerprint.entity.OutingCycle;
import com.example.fingerprint_backend.domain.fingerprint.event.DailyStatsUpdateEvent;
import com.example.fingerprint_backend.domain.fingerprint.exception.CycleException;
import com.example.fingerprint_backend.domain.fingerprint.service.classClosingTime.ClassClosingTimeQueryService;
import com.example.fingerprint_backend.domain.fingerprint.service.log.LogService;
import com.example.fingerprint_backend.entity.MemberEntity;
import com.example.fingerprint_backend.entity.SchoolClass;
import com.example.fingerprint_backend.service.Member.ClassQueryService;
import com.example.fingerprint_backend.service.Member.MemberQueryService;
import com.example.fingerprint_backend.types.LogAction;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CycleApplicationService {

    private final AttendanceCycleCommandService attendanceCycleCommandService;
    private final AttendanceCycleQueryService attendanceCycleQueryService;
    private final MemberQueryService memberQueryService;
    private final LogService logService;
    private final ApplicationEventPublisher eventPublisher;
    private final ClassQueryService classQueryService;
    private final OutingCycleCommandService outingCycleCommandService;
    private final OutingCycleQueryService outingCycleQueryService;
    private final ClassClosingTimeQueryService classClosingTimeQueryService;

    /**
     * 등교시 학생 번호와 시간을 입력받아 출석 주기를 생성합니다.
     *
     * @param studentNumber 학생 번호
     * @param attendTime    출석 시간
     */
    public AttendanceCycle createAttendanceCycle(String studentNumber, LocalDateTime attendTime) {

        // 만약 이전에 하교를 하지 않았다면, 해당 출석 주기는 강제 종료합니다.
        AttendanceCycle latestOpenCycle = attendanceCycleQueryService.getLatestOpenCycle(studentNumber);
        if (latestOpenCycle != null) {
            forceCloseAttendanceCycle(latestOpenCycle);
        }

        return attendanceCycleCommandService.createAttendanceCycle(studentNumber, attendTime);
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
     */
    public AttendanceCycle closeAttendanceCycle(String studentNumber, LocalDateTime leaveTime) {
        MemberEntity member = memberQueryService.getMemberByStudentNumber(studentNumber);

        AttendanceCycle openAttendCycle = getOrCreateLatestOpenCycle(studentNumber, leaveTime);

        Optional<ClassClosingTime> classClosingTime = classClosingTimeQueryService.getClassClosingTimeByTimeRange(member.getSchoolClass().getId(), openAttendCycle.getAttendTime(), leaveTime);

        if (classClosingTime.isPresent() && leaveTime.isAfter(classClosingTime.get().getClosingTime().plusMinutes(10))) {
            // 등교와 하교 사이에 문이 닫힌 시간이 있고, 하교 시간이 문 닫힌 시간보다 10분 이상 지난 경우
            openAttendCycle = createAttendanceCycle(studentNumber, leaveTime);
        }

        List<OutingCycle> outingCycles = openAttendCycle.getOutingCycles();

        LocalDateTime localDateTime = outingCycleCommandService.forceCloseAllOutingCycles(outingCycles);

        if (localDateTime != null && localDateTime.isAfter(leaveTime)) {
            throw new CycleException("하교 시간이 복귀 시간보다 늦을 수 없습니다.");
        }

        openAttendCycle.setLeaveTime(leaveTime);

        eventPublisher.publishEvent(new DailyStatsUpdateEvent(openAttendCycle));

        return openAttendCycle;
    }

    /**
     * 출석 사이클을 강제로 종료합니다.
     *
     * <p>출석 사이클의 종료 시간을 출석 시작 시간 또는 가장 늦은 외출 복귀 시간으로 설정합니다.</p>
     *
     * @param attendanceCycle 출석 사이클
     * @return 출석 기록
     */
    public AttendanceCycle forceCloseAttendanceCycle(AttendanceCycle attendanceCycle) {

//        내부의 외출 사이클을 종료하고 그중 가장 늦은 시간에 맞춰 하교 시간을 설정합니다.
        List<OutingCycle> outingCycles = attendanceCycle.getOutingCycles();

        LocalDateTime localDateTime = outingCycleCommandService.forceCloseAllOutingCycles(outingCycles);

        if (localDateTime != null) {
            attendanceCycle.setLeaveTime(localDateTime);
        } else {
            // 외출 사이클이 없는 경우 출석 시간을 하교 시간으로 설정합니다.
            attendanceCycle.setLeaveTime(attendanceCycle.getAttendTime());
        }

        eventPublisher.publishEvent(new DailyStatsUpdateEvent(attendanceCycle));

        return attendanceCycle;
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

        AttendanceCycle openCycle = getOrCreateLatestOpenCycle(studentNumber, outingStartTime);

        OutingCycle outingCycle = outingCycleCommandService.createOutingCycle(studentNumber, outingStartTime, reason);

        attendanceCycleCommandService.addOutingCycle(openCycle, outingCycle);

        return outingCycle;
    }

    /**
     * 외출 사이클을 종료합니다. (만약 생성된 외출 사이클이 없다면 생성합니다.)
     *
     * @param studentNumber 학번
     * @param outingEndTime 외출 종료 시간
     * @return 외출 기록
     */
    public OutingCycle closeOutingCycle(String studentNumber, LocalDateTime outingEndTime) {

        OutingCycle outingCycle = outingCycleQueryService.getLatestOpenOutingCycle(studentNumber);

        if (outingCycle == null) {
            // 열려있는 외출 사이클이 없는 경우
            outingCycle = createOutingCycle(studentNumber, outingEndTime, LogAction.기타);
        }

        return outingCycleCommandService.closeOutingCycle(outingCycle, outingEndTime);
    }

    /**
     * 반 객체로 모든 출석 사이클을 종료합니다.
     *
     * @param schoolClass 반 객체
     */
    public void classAllCycleByClass(SchoolClass schoolClass) {

        List<String> studentNumbers = schoolClass.getMembers().stream()
                .map(MemberEntity::getStudentNumber)
                .toList();

        List<AttendanceCycle> attendanceCycleList = attendanceCycleQueryService.getAllOpenCyclesByStudentNumber(studentNumbers);

        for (AttendanceCycle attendanceCycle : attendanceCycleList) {
            forceCloseAttendanceCycle(attendanceCycle);
        }
    }

}
