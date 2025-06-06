package com.example.fingerprint_backend.domain.fingerprint.service.stats;

import com.example.fingerprint_backend.domain.fingerprint.entity.*;
import com.example.fingerprint_backend.domain.fingerprint.event.MonthlyStatsUpdateEvent;
import com.example.fingerprint_backend.domain.fingerprint.event.WeeklyStatsUpdateEvent;
import com.example.fingerprint_backend.domain.fingerprint.exception.StatsException;
import com.example.fingerprint_backend.domain.fingerprint.service.log.LogService;
import com.example.fingerprint_backend.domain.fingerprint.types.PeriodType;
import com.example.fingerprint_backend.domain.fingerprint.util.DatePolicy;
import com.example.fingerprint_backend.domain.fingerprint.util.StatsCalculator;
import com.example.fingerprint_backend.domain.fingerprint.util.TimePolicy;
import com.example.fingerprint_backend.domain.fingerprint.vo.StatsUpdateValue;
import com.example.fingerprint_backend.domain.fingerprint.types.LogAction;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class StatsApplicationService {

    private final DailyStatsCommandService dailyStatsCommandService;
    private final DailyStatsQueryService dailyStatsQueryService;
    private final WeeklyStatsQueryService weeklyStatsQueryService;
    private final WeeklyStatsCommandService weeklyStatsCommandService;
    private final MonthlyStatsQueryService monthlyStatsQueryService;
    private final MonthlyStatsCommandService monthlyStatsCommandService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final LogService logService;


    /**
     * 출석 사이클 종료 시 일일 통계 업데이트
     *
     * @param attendanceCycle 출석 사이클
     * @param dailyStatsList  일일 통계 리스트
     */
    public void updateDailyStats(AttendanceCycle attendanceCycle, List<DailyStats> dailyStatsList) {

        Set<LocalDate> weeklyDates = new HashSet<>();
        Set<LocalDate> monthlyDates = new HashSet<>();
        String studentNumber = attendanceCycle.getStudentNumber();

        for (DailyStats dailyStats : dailyStatsList) {

            Long durationTime = StatsCalculator.getDailyDurationTimeForCycle(attendanceCycle, dailyStats.getEffectiveDate());
            dailyStats.updateStayDuration(durationTime);
            dailyStats.updateOutDuration(attendanceCycle.getTotalOutingDuration());

            weeklyDates.add(DatePolicy.getDateOfWeekDay(dailyStats.getEffectiveDate(), DayOfWeek.MONDAY));
            monthlyDates.add(DatePolicy.getMonthStartDate(dailyStats.getEffectiveDate()));
        }

//        일일 통계 업데이트 저장
        dailyStatsCommandService.saveAll(dailyStatsList);

        weeklyDates.forEach(date -> {
                    applicationEventPublisher.publishEvent(new WeeklyStatsUpdateEvent(studentNumber, date));
                }
        );

        monthlyDates.forEach(date -> {
                    applicationEventPublisher.publishEvent(new MonthlyStatsUpdateEvent(studentNumber, date));
                }
        );

    }

    /**
     * 주어진 출석 사이클에 해당하는 DailyStats를 가져오거나 생성합니다.
     *
     * @param attendanceCycle 출석 사이클
     * @return DailyStats 리스트
     */
    public List<DailyStats> getOrCreateDailyStatsInCycle(
            AttendanceCycle attendanceCycle
    ) {

        String studentNumber = attendanceCycle.getStudentNumber();
        LocalDate attendDate = TimePolicy.getLocalDate(attendanceCycle.getAttendTime());
        LocalDate leaveDate = TimePolicy.getLocalDate(attendanceCycle.getLeaveTime());

        List<DailyStats> dailyStatsList = new ArrayList<>();
        for (LocalDate date = attendDate; !date.isAfter(leaveDate); date = date.plusDays(1)) {
            DailyStats dailyStats = dailyStatsCommandService.getOrCreateDailyStats(studentNumber, date);
            dailyStatsList.add(dailyStats);
        }

        return dailyStatsList;
    }

    /**
     * WeeklyStats, MonthlyStats 업데이트
     *
     * @param stats            ContinuousStats
     * @param statsUpdateValue 업데이트 값 (체류 시간, 외출 시간, 출석 횟수, 평균 등교 시간, 평균 하교 시간)
     */
    public void setStats(ContinuousStats stats, StatsUpdateValue statsUpdateValue) {

        if (stats instanceof WeeklyStats weeklyStats) {
            weeklyStatsCommandService.setStayDuration(weeklyStats, statsUpdateValue.stayDuration());
            weeklyStatsCommandService.setOutDuration(weeklyStats, statsUpdateValue.outDuration());
            weeklyStatsCommandService.setAttendCount(weeklyStats, statsUpdateValue.attendCount());
            weeklyStatsCommandService.setAvgAttendTime(weeklyStats, statsUpdateValue.averageAttendTime());
            weeklyStatsCommandService.setAvgLeaveTime(weeklyStats, statsUpdateValue.averageLeaveTime());

        } else if (stats instanceof MonthlyStats monthlyStats) {
            monthlyStatsCommandService.setStayDuration(monthlyStats, statsUpdateValue.stayDuration());
            monthlyStatsCommandService.setOutDuration(monthlyStats, statsUpdateValue.outDuration());
            monthlyStatsCommandService.setAttendCount(monthlyStats, statsUpdateValue.attendCount());
            monthlyStatsCommandService.setAvgAttendTime(monthlyStats, statsUpdateValue.averageAttendTime());
            monthlyStatsCommandService.setAvgLeaveTime(monthlyStats, statsUpdateValue.averageLeaveTime());

        } else {
            throw new StatsException("지원하지 않는 통계 타입입니다.");
        }
    }

    /**
     * 주간 통계 업데이트
     *
     * @param studentNumber 학번
     * @param date          날짜
     * @return 주간 통계
     */
    public WeeklyStats updateWeeklyStats(String studentNumber, LocalDate date) {

        WeeklyStats weeklyStats = weeklyStatsCommandService.getOrCreateWeeklyStats(studentNumber, date);
        List<DailyStats> dailyStatsForWeek = dailyStatsQueryService.getDailyStatsForWeek(studentNumber, date);
        List<LogEntity> attendLogs = logService.getLogsInRangeByStudentNumberAndAction(
                studentNumber,
                LogAction.등교,
                TimePolicy.getStartDateTime(weeklyStats.getEffectiveDate()),
                TimePolicy.getEndDateTime(weeklyStats.getEndDate()));
        List<LogEntity> leaveLogs = logService.getLogsInRangeByStudentNumberAndAction(
                studentNumber,
                LogAction.하교,
                TimePolicy.getStartDateTime(weeklyStats.getEffectiveDate()),
                TimePolicy.getEndDateTime(weeklyStats.getEndDate()));

        // 업데이트 값 계산
        StatsUpdateValue statsUpdateValue = StatsCalculator.calculateStatsUpdateValue(dailyStatsForWeek, attendLogs, leaveLogs);

        setStats(weeklyStats, statsUpdateValue);

        return weeklyStats;

    }

    /**
     * 월간 통계 업데이트
     *
     * @param studentNumber 학번
     * @param date          날짜
     * @return 월간 통계
     */
    public MonthlyStats updateMonthlyStats(String studentNumber, LocalDate date) {

        MonthlyStats monthlyStats = monthlyStatsCommandService.getOrCreateMonthlyStats(studentNumber, date);
        List<DailyStats> dailyStatsForMonth = dailyStatsQueryService.getDailyStatsForMonth(studentNumber, date);
        List<LogEntity> attendLogs = logService.getLogsInRangeByStudentNumberAndAction(
                studentNumber,
                LogAction.등교,
                TimePolicy.getStartDateTime(monthlyStats.getEffectiveDate()),
                TimePolicy.getEndDateTime(monthlyStats.getEndDate()));
        List<LogEntity> leaveLogs = logService.getLogsInRangeByStudentNumberAndAction(
                studentNumber,
                LogAction.하교,
                TimePolicy.getStartDateTime(monthlyStats.getEffectiveDate()),
                TimePolicy.getEndDateTime(monthlyStats.getEndDate()));

        // 업데이트 값 계산
        StatsUpdateValue statsUpdateValue = StatsCalculator.calculateStatsUpdateValue(dailyStatsForMonth, attendLogs, leaveLogs);

        setStats(monthlyStats, statsUpdateValue);

        return monthlyStats;

    }

    /**
     * 랭킹에서 기간 타입과 날짜로 체류시간 순서로 통계 리스트를 가져오는 메소드
     *
     * @param periodType 기간 타입
     * @param date       날짜
     * @return 통계 리스트
     */
    public List<? extends BaseStats> getStatsOrderedByStayDuration(PeriodType periodType, LocalDate date) {

        return switch (periodType) {
            case 일간 -> dailyStatsQueryService.getDailyStatsOrderedByStayDuration(date);
            case 주간 -> weeklyStatsQueryService.getWeeklyStatsOrderedByStayDuration(date);
            case 월간 -> monthlyStatsQueryService.getMonthlyStatsOrderedByStayDuration(date);
            case 전체 -> List.of();
        };
    }

    /**
     * 랭킹에서 기간 타입과 날짜로 출석시간 순서로 통계 리스트를 가져오는 메소드
     *
     * @param periodType 기간 타입
     * @param date       날짜
     * @return 통계 리스트
     */
    public List<? extends BaseStats> getStatsOrderedByAttendanceTime(PeriodType periodType, LocalDate date) {

//        TODO 일간 통계 수정 필요

        return switch (periodType) {
            case 일간 -> List.of();
            case 주간 -> weeklyStatsQueryService.getWeeklyStatsOrderedByAttendanceTime(date);
            case 월간 -> monthlyStatsQueryService.getMonthlyStatsOrderedByAttendanceTime(date);
            case 전체 -> null;
        };
    }
}
