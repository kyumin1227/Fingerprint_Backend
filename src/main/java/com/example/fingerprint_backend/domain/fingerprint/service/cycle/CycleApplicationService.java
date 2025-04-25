package com.example.fingerprint_backend.domain.fingerprint.service.cycle;

import com.example.fingerprint_backend.domain.fingerprint.entity.AttendanceCycle;
import com.example.fingerprint_backend.domain.fingerprint.entity.ClassClosingTime;
import com.example.fingerprint_backend.domain.fingerprint.entity.OutingCycle;
import com.example.fingerprint_backend.domain.fingerprint.event.DailyStatsUpdateEvent;
import com.example.fingerprint_backend.domain.fingerprint.exception.CycleException;
import com.example.fingerprint_backend.domain.fingerprint.service.LogService;
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

    /**
     * 외출 사이클을 생성합니다. (만약 등교 사이클이 없다면 생성합니다.)
     *
     * @param studentNumber   학번
     * @param outingStartTime 외출 시작 시간
     * @param reason          외출 사유
     * @return 외출 기록
     */
    public OutingCycle createOutingCycle(String studentNumber, LocalDateTime outingStartTime, LogAction reason) {

        AttendanceCycle openCycle = attendanceCycleCommandService.getOrCreateLatestOpenCycle(studentNumber, outingStartTime);

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
     * 하교시 학생 번호와 시간을 입력받아 출석 주기를 업데이트합니다.
     *
     * @param studentNumber 학생 번호
     * @param leaveTime     하교 시간
     * @throws CycleException 하교 처리할 등교 기록이 없을 경우
     */
    public AttendanceCycle closeAttendanceCycle(String studentNumber, LocalDateTime leaveTime) {
        MemberEntity member = memberQueryService.getMemberByStudentNumber(studentNumber);

        AttendanceCycle openAttendCycle = attendanceCycleCommandService.getOrCreateLatestOpenCycle(studentNumber, leaveTime);

        Optional<ClassClosingTime> classClosingTime = logService.getClassClosingTimeByTimeAfter(member.getSchoolClass().getId(), openAttendCycle.getAttendTime());

        if (classClosingTime.isPresent() && leaveTime.isAfter(classClosingTime.get().getClosingTime().plusMinutes(10))) {
            // 등교와 하교 사이에 문이 닫힌 시간이 있고, 하교 시간이 문 닫힌 시간보다 10분 이상 지난 경우
            openAttendCycle = attendanceCycleCommandService.createAttendanceCycle(studentNumber, leaveTime);
        }

        openAttendCycle.setLeaveTime(leaveTime);

        eventPublisher.publishEvent(new DailyStatsUpdateEvent(openAttendCycle));

        return openAttendCycle;
    }

    public void classAllCycleByClassId(Long classId) {

        SchoolClass schoolClass = classQueryService.getClassById(classId);

        List<String> studentNumbers = schoolClass.getMembers().stream()
                .map(MemberEntity::getStudentNumber)
                .toList();

        List<AttendanceCycle> attendanceCycles = attendanceCycleQueryService.getAllOpenCyclesByStudentNumber(studentNumbers);
    }

}
