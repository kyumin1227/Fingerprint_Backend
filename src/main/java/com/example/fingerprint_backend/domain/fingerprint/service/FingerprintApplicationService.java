package com.example.fingerprint_backend.domain.fingerprint.service;

import com.example.fingerprint_backend.types.LogAction;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class FingerprintApplicationService {

    private final CycleQueryService cycleQueryService;
    private final CycleCommandService cycleCommandService;

    public FingerprintApplicationService(CycleQueryService cycleQueryService, CycleCommandService cycleCommandService) {
        this.cycleQueryService = cycleQueryService;
        this.cycleCommandService = cycleCommandService;
    }

    /**
     * 로그를 등록하면 등교, 하교, 외출 등에 따라 서로 다른 메소드를 호출하는 메소드
     */
    public void routeLog(String studentNumber, LogAction logAction) {
        switch (logAction) {
            case 등교 -> attendanceLog(studentNumber, LocalDateTime.now());
            case 하교 -> leaveLog(studentNumber, LocalDateTime.now());
            case 복귀 -> returnLog(studentNumber, LocalDateTime.now());
            case 식사, 기타, 도서관 -> outingLog(studentNumber, LocalDateTime.now());
            default -> throw new IllegalArgumentException("잘못된 로그 액션입니다.");
        }
    }

    /**
     * 등교 로그를 처리하는 메소드
     *
     * @param studentNumber 학번
     * @param attendTime    출석 시간
     */
    private void attendanceLog(String studentNumber, LocalDateTime attendTime) {
        cycleCommandService.createAttendanceCycle(studentNumber, attendTime);
    }

    /**
     * 하교 로그를 처리하는 메소드
     *
     * @param studentNumber 학번
     * @param leaveTime     하교 시간
     */
    private void leaveLog(String studentNumber, LocalDateTime leaveTime) {
        cycleCommandService.updateAttendanceCycle(studentNumber, leaveTime);
    }

    /**
     * 외출 복귀 로그를 처리하는 메소드
     *
     * @param studentNumber 학번
     * @param returnTime    복귀 시간
     */
    private void returnLog(String studentNumber, LocalDateTime returnTime) {
        cycleCommandService.updateOutingCycle(studentNumber, returnTime);
    }

    /**
     * 외출 로그를 처리하는 메소드
     *
     * @param studentNumber 학번
     * @param outingTime    외출 시간
     */
    private void outingLog(String studentNumber, LocalDateTime outingTime) {
        cycleCommandService.createOutingCycle(studentNumber, outingTime);
    }
}
