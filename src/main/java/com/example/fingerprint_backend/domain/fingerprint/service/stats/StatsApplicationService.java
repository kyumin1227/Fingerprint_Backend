package com.example.fingerprint_backend.domain.fingerprint.service.stats;

import com.example.fingerprint_backend.domain.fingerprint.entity.DailyStats;
import com.example.fingerprint_backend.domain.fingerprint.entity.OutingCycle;
import com.example.fingerprint_backend.util.DatePolicy;
import com.example.fingerprint_backend.util.TimePolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.example.fingerprint_backend.domain.fingerprint.entity.AttendanceCycle;

import java.sql.Time;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsApplicationService {

    private final DailyStatsCommandService dailyStatsCommandService;
    private final DailyStatsQueryService dailyStatsQueryService;

    /**
     * 주어진 출석 사이클에 해당하는 DailyStats를 가져오거나 생성합니다.
     *
     * @param attendanceCycle 출석 사이클
     * @return DailyStats 리스트
     */
    public List<DailyStats> getOrCreateDailyStatsInRange(
            AttendanceCycle attendanceCycle
    ) {

        String studentNumber = attendanceCycle.getStudentNumber();
        LocalDate attendDate = TimePolicy.getLocalDate(attendanceCycle.getAttendTime());
        LocalDate leaveDate = TimePolicy.getLocalDate(attendanceCycle.getLeaveTime());

        List<DailyStats> dailyStatsList = new ArrayList<>();
        for (LocalDate date = attendDate; !date.isAfter(leaveDate); date = date.plusDays(1)) {
            DailyStats dailyStats = dailyStatsQueryService.getDailyStatsByStudentNumberAndDate(studentNumber, date);
            if (dailyStats == null) {
                dailyStats = dailyStatsCommandService.createDailyStats(studentNumber, date);
            }
            dailyStatsList.add(dailyStats);
        }

        return dailyStatsList;
    }

    /**
     * 주어진 외출 사이클에 해당하는 DailyStats를 가져오거나 생성합니다.
     *
     * @param outingCycle 외출 사이클
     * @return DailyStats 리스트
     */
    public List<DailyStats> getOrCreateDailyStatsInRange(
            OutingCycle outingCycle
    ) {

        String studentNumber = outingCycle.getStudentNumber();
        LocalDate startDate = TimePolicy.getLocalDate(outingCycle.getOutingStartTime());
        LocalDate endDate = TimePolicy.getLocalDate(outingCycle.getOutingEndTime());

        List<DailyStats> dailyStatsList = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            DailyStats dailyStats = dailyStatsQueryService.getDailyStatsByStudentNumberAndDate(studentNumber, date);
            if (dailyStats == null) {
                dailyStats = dailyStatsCommandService.createDailyStats(studentNumber, date);
            }
            dailyStatsList.add(dailyStats);
        }

        return dailyStatsList;
    }

    /**
     * 출석 사이클에서 해당 날짜에 대한 체류 시간을 계산합니다.
     *
     * @param attendanceCycle 출석 사이클
     * @param date            계산 날짜
     * @return Long 체류 시간 (밀리초 단위)
     */
    public Long getDailyDurationTimeForCycle(AttendanceCycle attendanceCycle, LocalDate date) {

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
    public Long getDailyOutingTimeForCycle(OutingCycle outingCycle, LocalDate date) {

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
}
