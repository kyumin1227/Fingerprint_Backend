package com.example.fingerprint_backend.domain.fingerprint.vo;

import java.time.LocalTime;

/**
 * WeeklyStats, MonthlyStats 통계 업데이트 값
 */
public record StatsUpdateValue(
        Long stayDuration,
        Long outDuration,
        Integer attendCount,
        LocalTime averageAttendTime,
        LocalTime averageLeaveTime
) {
    
}
