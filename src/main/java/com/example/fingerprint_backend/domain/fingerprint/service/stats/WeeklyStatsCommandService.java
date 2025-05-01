package com.example.fingerprint_backend.domain.fingerprint.service.stats;

import com.example.fingerprint_backend.domain.fingerprint.entity.WeeklyStats;
import com.example.fingerprint_backend.domain.fingerprint.repository.WeeklyStatsRepository;
import com.example.fingerprint_backend.domain.fingerprint.util.DatePolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

@Service
@Transactional
@RequiredArgsConstructor
public class WeeklyStatsCommandService {

    private final WeeklyStatsRepository weeklyStatsRepository;
    private final WeeklyStatsQueryService weeklyStatsQueryService;

    /**
     * 주간 통계 생성
     *
     * @param studentNumber 학번
     * @param date          시작 날짜
     * @return 주간 통계
     */
    public WeeklyStats createWeeklyStats(String studentNumber, LocalDate date) {

        LocalDate startDate = DatePolicy.getDateOfWeekDay(date, DayOfWeek.MONDAY);

        WeeklyStats weeklyStats = WeeklyStats.builder()
                .studentNumber(studentNumber)
                .startDate(startDate)
                .build();

        return weeklyStatsRepository.save(weeklyStats);
    }

    /**
     * 주어진 학생 번호와 날짜에 해당하는 WeeklyStats를 가져오거나 생성합니다.
     *
     * @param studentNumber 학생 번호
     * @param date          날짜
     * @return WeeklyStats
     */
    public WeeklyStats getOrCreateWeeklyStats(String studentNumber, LocalDate date) {

        LocalDate weekStartDate = DatePolicy.getDateOfWeekDay(date, DayOfWeek.MONDAY);

        return weeklyStatsQueryService.getWeeklyStatsByStudentNumberAndDate(studentNumber, weekStartDate)
                .orElseGet(() -> createWeeklyStats(studentNumber, weekStartDate));
    }

    /**
     * 주간 통계의 체류 시간을 재설정 합니다.
     *
     * @param weeklyStats 주간 통계
     * @param stayDuration  체류 시간
     * @return 주간 통계
     */
    public WeeklyStats setStayDuration(WeeklyStats weeklyStats, Long stayDuration) {

        weeklyStats.setStayDuration(stayDuration);
        return weeklyStats;
    }

    /**
     * 주간 통계의 외출 시간을 재설정 합니다.
     *
     * @param weeklyStats 주간 통계
     * @param outDuration  외출 시간
     * @return 주간 통계
     */
    public WeeklyStats setOutDuration(WeeklyStats weeklyStats, Long outDuration) {

        weeklyStats.setOutDuration(outDuration);
        return weeklyStats;
    }

    /**
     * 주간 통계의 출석 횟수를 재설정 합니다.
     *
     * @param weeklyStats 주간 통계
     * @param attendCount  출석 횟수
     * @return 주간 통계
     */
    public WeeklyStats setAttendCount(WeeklyStats weeklyStats, Integer attendCount) {

        weeklyStats.setTotalAttendCount(attendCount);
        return weeklyStats;
    }

    /**
     * 주간 통계의 평균 출석 시간을 재설정 합니다.
     *
     * @param weeklyStats  주간 통계
     * @param avgAttendTime 평균 출석 시간
     * @return 주간 통계
     */
    public WeeklyStats setAvgAttendTime(WeeklyStats weeklyStats, LocalTime avgAttendTime) {

        weeklyStats.setAvgAttendTime(avgAttendTime);
        return weeklyStats;
    }

    /**
     * 주간 통계의 평균 퇴실 시간을 재설정 합니다.
     *
     * @param weeklyStats  주간 통계
     * @param avgLeaveTime  평균 퇴실 시간
     * @return 주간 통계
     */
    public WeeklyStats setAvgLeaveTime(WeeklyStats weeklyStats, LocalTime avgLeaveTime) {

        weeklyStats.setAvgLeaveTime(avgLeaveTime);
        return weeklyStats;
    }


}
