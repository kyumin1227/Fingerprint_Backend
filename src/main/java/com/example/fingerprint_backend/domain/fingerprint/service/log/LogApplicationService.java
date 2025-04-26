package com.example.fingerprint_backend.domain.fingerprint.service.log;

import com.example.fingerprint_backend.domain.fingerprint.entity.AttendanceCycle;
import com.example.fingerprint_backend.domain.fingerprint.entity.OutingCycle;
import com.example.fingerprint_backend.domain.fingerprint.service.cycle.CycleApplicationService;
import com.example.fingerprint_backend.service.Member.MemberQueryService;
import com.example.fingerprint_backend.types.LogAction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LogApplicationService {

    private final LogService logService;
    private final MemberQueryService memberQueryService;
    private final CycleApplicationService cycleApplicationService;

    /**
     * 로그를 등록하면 등교, 하교, 외출 등에 따라 서로 다른 메소드를 호출하는 메소드
     */
    public Object routeLog(String studentNumber, LogAction logAction, LocalDateTime logTime) {
        memberQueryService.getMemberByStudentNumber(studentNumber);

//       TODO - 문자열 반환 (ex - 하교 시 체류 시간)

        return switch (logAction) {
            case 등교 -> attendanceLog(studentNumber, logTime);
            case 하교 -> leaveLog(studentNumber, logTime);
            case 복귀 -> returnLog(studentNumber, logTime);
            case 식사, 기타, 도서관 -> outingLog(studentNumber, logTime, logAction);
        };
    }

    /**
     * 등교 로그를 처리하는 메소드
     *
     * @param studentNumber 학번
     * @param attendTime    출석 시간
     */
    private AttendanceCycle attendanceLog(String studentNumber, LocalDateTime attendTime) {
        logService.createLog(studentNumber, LogAction.등교);
        return cycleApplicationService.createAttendanceCycle(studentNumber, attendTime);
    }

    /**
     * 하교 로그를 처리하는 메소드
     *
     * @param studentNumber 학번
     * @param leaveTime     하교 시간
     */
    private AttendanceCycle leaveLog(String studentNumber, LocalDateTime leaveTime) {
        logService.createLog(studentNumber, LogAction.하교);
        return cycleApplicationService.closeAttendanceCycle(studentNumber, leaveTime);
    }

    /**
     * 외출 로그를 처리하는 메소드
     *
     * @param studentNumber 학번
     * @param outingTime    외출 시간
     */
    private OutingCycle outingLog(String studentNumber, LocalDateTime outingTime, LogAction logAction) {
        logService.createLog(studentNumber, logAction);
        return cycleApplicationService.createOutingCycle(studentNumber, outingTime, logAction);
    }

    /**
     * 외출 복귀 로그를 처리하는 메소드
     *
     * @param studentNumber 학번
     * @param returnTime    복귀 시간
     */
    private OutingCycle returnLog(String studentNumber, LocalDateTime returnTime) {
        logService.createLog(studentNumber, LogAction.복귀);
        return cycleApplicationService.closeOutingCycle(studentNumber, returnTime);
    }

}
