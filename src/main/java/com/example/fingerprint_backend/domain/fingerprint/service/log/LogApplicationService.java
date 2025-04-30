package com.example.fingerprint_backend.domain.fingerprint.service.log;

import com.example.fingerprint_backend.ApiResponse;
import com.example.fingerprint_backend.domain.fingerprint.entity.AttendanceCycle;
import com.example.fingerprint_backend.domain.fingerprint.entity.OutingCycle;
import com.example.fingerprint_backend.domain.fingerprint.entity.Ranking;
import com.example.fingerprint_backend.domain.fingerprint.service.cycle.CycleApplicationService;
import com.example.fingerprint_backend.domain.fingerprint.service.ranking.RankingApplicationService;
import com.example.fingerprint_backend.domain.fingerprint.util.FormatPolicy;
import com.example.fingerprint_backend.service.Member.MemberQueryService;
import com.example.fingerprint_backend.types.LogAction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class LogApplicationService {

    private final LogService logService;
    private final MemberQueryService memberQueryService;
    private final CycleApplicationService cycleApplicationService;
    private final RankingApplicationService rankingApplicationService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * 로그를 등록하면 등교, 하교, 외출 등에 따라 서로 다른 메소드를 호출하는 메소드
     */
    public ApiResponse routeLog(String studentNumber, LogAction logAction, LocalDateTime logTime) {

        memberQueryService.getMemberByStudentNumber(studentNumber);

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
    private ApiResponse attendanceLog(String studentNumber, LocalDateTime attendTime) {

        logService.createLog(studentNumber, LogAction.등교);
        Ranking ranking = rankingApplicationService.createDailyAttendanceRanking(studentNumber, attendTime);
        AttendanceCycle attendanceCycle = cycleApplicationService.createAttendanceCycle(studentNumber, attendTime);

        String message = String.format("%s, 등교 시각: %s (%d등)\n등교 처리 되었습니다.",
                studentNumber, attendTime.toLocalTime().format(formatter), ranking.getRank_order());

        return new ApiResponse(true, message, attendanceCycle);
    }

    /**
     * 하교 로그를 처리하는 메소드
     *
     * @param studentNumber 학번
     * @param leaveTime     하교 시간
     */
    private ApiResponse leaveLog(String studentNumber, LocalDateTime leaveTime) {

        logService.createLog(studentNumber, LogAction.하교);
        AttendanceCycle attendanceCycle = cycleApplicationService.closeAttendanceCycle(studentNumber, leaveTime);

        String message = String.format("%s, 하교 시각: %s\n체류 시간: %s, 외출 시간: %s",
                studentNumber, leaveTime.toLocalTime().format(formatter), FormatPolicy.formatTime(attendanceCycle.getTotalStayDuration()),
                FormatPolicy.formatTime(attendanceCycle.getTotalOutingDuration()));

        return new ApiResponse(true, message, attendanceCycle);
    }

    /**
     * 외출 로그를 처리하는 메소드
     *
     * @param studentNumber 학번
     * @param outingTime    외출 시간
     */
    private ApiResponse outingLog(String studentNumber, LocalDateTime outingTime, LogAction logAction) {

        logService.createLog(studentNumber, logAction);
        OutingCycle outingCycle = cycleApplicationService.createOutingCycle(studentNumber, outingTime, logAction);

        String message = String.format("%s, 외출 시각: %s\n사유: %s, 외출 처리 되었습니다.",
                studentNumber, outingTime.toLocalTime().format(formatter), logAction.toString());

        return new ApiResponse(true, message, outingCycle);
    }

    /**
     * 외출 복귀 로그를 처리하는 메소드
     *
     * @param studentNumber 학번
     * @param returnTime    복귀 시간
     */
    private ApiResponse returnLog(String studentNumber, LocalDateTime returnTime) {
        
        logService.createLog(studentNumber, LogAction.복귀);
        OutingCycle outingCycle = cycleApplicationService.closeOutingCycle(studentNumber, returnTime);

        String message = String.format("%s, 복귀 시각: %s\n외출 시간: %s, 복귀 처리 되었습니다.",
                studentNumber, returnTime.toLocalTime().format(formatter), FormatPolicy.formatTime(outingCycle.getTotalOutingDuration()));

        return new ApiResponse(true, message, outingCycle);
    }

}
