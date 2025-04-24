package com.example.fingerprint_backend.domain.fingerprint.service.stats;

import com.example.fingerprint_backend.domain.fingerprint.entity.*;
import com.example.fingerprint_backend.domain.fingerprint.event.MonthlyStatsUpdateEvent;
import com.example.fingerprint_backend.domain.fingerprint.event.WeeklyStatsUpdateEvent;
import com.example.fingerprint_backend.domain.fingerprint.repository.DailyStatsRepository;
import com.example.fingerprint_backend.domain.fingerprint.service.LogService;
import com.example.fingerprint_backend.domain.fingerprint.util.DatePolicy;
import com.example.fingerprint_backend.domain.fingerprint.util.StatsCalculator;
import com.example.fingerprint_backend.domain.fingerprint.util.TimePolicy;
import com.example.fingerprint_backend.domain.fingerprint.vo.StatsUpdateValue;
import com.example.fingerprint_backend.types.LogAction;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class StatsApplicationService {

    private final DailyStatsCommandService dailyStatsCommandService;
    private final DailyStatsQueryService dailyStatsQueryService;
    private final WeeklyStatsQueryService weeklyStatsQueryService;
    private final WeeklyStatsCommandService weeklyStatsCommandService;
    private final MonthlyStatsQueryService monthlyStatsQueryService;
    private final MonthlyStatsCommandService monthlyStatsCommandService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final LogService logService;
    private final DailyStatsRepository dailyStatsRepository;


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
        dailyStatsRepository.saveAllAndFlush(dailyStatsList);

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
     * @param stats            BaseStats
     * @param statsUpdateValue 업데이트 값 (체류 시간, 외출 시간, 출석 횟수, 평균 등교 시간, 평균 하교 시간)
     */
    public void setStats(BaseStats stats, StatsUpdateValue statsUpdateValue) {

        stats.setTotalStayDuration(statsUpdateValue.stayDuration());
        stats.setTotalOutDuration(statsUpdateValue.outDuration());
        stats.setTotalAttendCount(statsUpdateValue.attendCount());
        stats.setAvgAttendTime(statsUpdateValue.averageAttendTime());
        stats.setAvgLeaveTime(statsUpdateValue.averageLeaveTime());
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
                TimePolicy.getStartDateTime(weeklyStats.getStartDate()),
                TimePolicy.getEndDateTime(weeklyStats.getEndDate()));
        List<LogEntity> leaveLogs = logService.getLogsInRangeByStudentNumberAndAction(
                studentNumber,
                LogAction.하교,
                TimePolicy.getStartDateTime(weeklyStats.getStartDate()),
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
                TimePolicy.getStartDateTime(monthlyStats.getStartDate()),
                TimePolicy.getEndDateTime(monthlyStats.getEndDate()));
        List<LogEntity> leaveLogs = logService.getLogsInRangeByStudentNumberAndAction(
                studentNumber,
                LogAction.하교,
                TimePolicy.getStartDateTime(monthlyStats.getStartDate()),
                TimePolicy.getEndDateTime(monthlyStats.getEndDate()));

        // 업데이트 값 계산
        StatsUpdateValue statsUpdateValue = StatsCalculator.calculateStatsUpdateValue(dailyStatsForMonth, attendLogs, leaveLogs);

        setStats(monthlyStats, statsUpdateValue);

        return monthlyStats;

    }

}
