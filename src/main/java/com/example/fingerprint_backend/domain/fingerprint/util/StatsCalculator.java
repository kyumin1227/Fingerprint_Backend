package com.example.fingerprint_backend.domain.fingerprint.util;

import com.example.fingerprint_backend.domain.fingerprint.entity.AttendanceCycle;
import com.example.fingerprint_backend.domain.fingerprint.entity.DailyStats;
import com.example.fingerprint_backend.domain.fingerprint.entity.LogEntity;
import com.example.fingerprint_backend.domain.fingerprint.entity.OutingCycle;
import com.example.fingerprint_backend.domain.fingerprint.vo.StatsUpdateValue;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class StatsCalculator {

    /**
     * 출석 사이클에서 해당 날짜에 대한 체류 시간을 계산합니다.
     *
     * @param attendanceCycle 출석 사이클
     * @param date            계산 날짜
     * @return Long 체류 시간 (밀리초 단위)
     */
    public static Long getDailyDurationTimeForCycle(AttendanceCycle attendanceCycle, LocalDate date) {

        LocalDateTime start = DatePolicy.max(
                attendanceCycle.getAttendTime(),
                TimePolicy.getStartDateTime(date)
        );

        LocalDateTime end = DatePolicy.min(
                attendanceCycle.getLeaveTime(),
                TimePolicy.getEndDateTime(date)
        );

        return Math.max(0, Duration.between(start, end).toMillis());
    }

    /**
     * 외출 사이클에서 해당 날짜에 대한 외출 시간을 계산합니다.
     *
     * @param outingCycle 외출 사이클
     * @param date        계산 날짜
     * @return Long 외출 시간 (밀리초 단위)
     */
    public static Long getDailyOutingTimeForCycle(OutingCycle outingCycle, LocalDate date) {

        LocalDateTime start = DatePolicy.max(
                outingCycle.getOutingStartTime(),
                TimePolicy.getStartDateTime(date)
        );

        LocalDateTime end = DatePolicy.min(
                outingCycle.getOutingEndTime(),
                TimePolicy.getEndDateTime(date)
        );

        return Math.max(0, Duration.between(start, end).toMillis());
    }

    /**
     * 일간 통계 리스트에서 총 체류 시간을 계산합니다.
     *
     * @param dailyStatsList 일일 통계 리스트
     * @return Long 총 체류 시간 (밀리초 단위)
     */
    public static Long getTotalStayDuration(List<DailyStats> dailyStatsList) {
        return dailyStatsList.stream()
                .mapToLong(DailyStats::getStayDuration)
                .sum();
    }

    /**
     * 일간 통계 리스트에서 총 외출 시간을 계산합니다.
     *
     * @param dailyStatsList 일일 통계 리스트
     * @return Long 총 외출 시간 (밀리초 단위)
     */
    public static Long getTotalOutDuration(List<DailyStats> dailyStatsList) {
        return dailyStatsList.stream()
                .mapToLong(DailyStats::getOutDuration)
                .sum();
    }

    /**
     * 로그 엔티티 리스트에서 평균 시간을 계산합니다.
     *
     * @param logEntities 로그 엔티티 리스트
     * @return LocalTime 평균 시간
     */
    public static LocalTime getAvgTime(List<LogEntity> logEntities) {
        if (logEntities.isEmpty()) {
            return LocalTime.of(0, 0, 0);
        }
        long totalSeconds = logEntities.stream()
                .map(logEntity ->
                        logEntity.getEventTime().toLocalTime()
                ).mapToLong(LocalTime::toSecondOfDay)
                .sum();
        long avgSeconds = totalSeconds / logEntities.size();
        return LocalTime.ofSecondOfDay(avgSeconds);
    }

    /**
     * WeeklyStats, MonthlyStats 업데이트 값 계산
     *
     * @param dailyStatsList 일일 통계 리스트
     * @param attendanceLogs 출석 로그 리스트
     * @param leaveLogs      하교 로그 리스트
     * @return StatsUpdateValue 업데이트 값 (체류 시간, 외출 시간, 출석 횟수, 평균 등교 시간, 평균 하교 시간)
     */
    public static StatsUpdateValue calculateStatsUpdateValue(
            List<DailyStats> dailyStatsList,
            List<LogEntity> attendanceLogs,
            List<LogEntity> leaveLogs
    ) {

        Long totalStayDuration = getTotalStayDuration(dailyStatsList);
        Long totalOutDuration = getTotalOutDuration(dailyStatsList);
        Integer totalAttendCount = dailyStatsList.size();
        LocalTime averageAttendTime = getAvgTime(attendanceLogs);
        LocalTime averageLeaveTime = getAvgTime(leaveLogs);

        System.out.println("totalStayDuration = " + totalStayDuration);
        System.out.println("totalOutDuration = " + totalOutDuration);

        return new StatsUpdateValue(
                totalStayDuration,
                totalOutDuration,
                totalAttendCount,
                averageAttendTime,
                averageLeaveTime
        );
    }

}
