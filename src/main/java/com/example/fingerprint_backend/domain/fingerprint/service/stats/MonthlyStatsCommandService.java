package com.example.fingerprint_backend.domain.fingerprint.service.stats;

import com.example.fingerprint_backend.domain.fingerprint.entity.MonthlyStats;
import com.example.fingerprint_backend.domain.fingerprint.repository.MonthlyStatsRepository;
import com.example.fingerprint_backend.domain.fingerprint.util.DatePolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
@Transactional
@RequiredArgsConstructor
public class MonthlyStatsCommandService {

    private final MonthlyStatsRepository monthlyStatsRepository;
    private final MonthlyStatsQueryService monthlyStatsQueryService;

    /**
     * 월간 통계 생성
     *
     * @param studentNumber 학번
     * @param date          시작 날짜
     * @return 월간 통계
     */
    public MonthlyStats createMonthlyStats(String studentNumber, LocalDate date) {

        LocalDate startDate = DatePolicy.getMonthStartDate(date);

        MonthlyStats monthlyStats = MonthlyStats.builder()
                .studentNumber(studentNumber)
                .startDate(startDate)
                .build();

        return monthlyStatsRepository.save(monthlyStats);
    }

    /**
     * 주어진 학생 번호와 날짜에 해당하는 MonthlyStats를 가져오거나 생성합니다.
     *
     * @param studentNumber 학생 번호
     * @param date          날짜
     * @return MonthlyStats
     */
    public MonthlyStats getOrCreateMonthlyStats(String studentNumber, LocalDate date) {

        LocalDate monthStartDate = DatePolicy.getMonthStartDate(date);

        return monthlyStatsQueryService.getMonthlyStatsByStudentNumberAndDate(studentNumber, monthStartDate)
                .orElseGet(() -> createMonthlyStats(studentNumber, monthStartDate));

    }

    /**
     * 월간 통계의 체류 시간을 재설정 합니다.
     *
     * @param monthlyStats 월간 통계
     * @param stayDuration  체류 시간
     * @return 월간 통계
     */
    public MonthlyStats setStayDuration(MonthlyStats monthlyStats, Long stayDuration) {
        monthlyStats.setStayDuration(stayDuration);
        return monthlyStats;
    }

    /**
     * 월간 통계의 외출 시간을 재설정 합니다.
     *
     * @param monthlyStats 월간 통계
     * @param outDuration  외출 시간
     * @return 월간 통계
     */
    public MonthlyStats setOutDuration(MonthlyStats monthlyStats, Long outDuration) {
        monthlyStats.setOutDuration(outDuration);
        return monthlyStats;
    }

    /**
     * 월간 통계의 출석 횟수를 재설정 합니다.
     *
     * @param monthlyStats 월간 통계
     * @param attendCount  출석 횟수
     * @return 월간 통계
     */
    public MonthlyStats setAttendCount(MonthlyStats monthlyStats, Integer attendCount) {
        monthlyStats.setTotalAttendCount(attendCount);
        return monthlyStats;
    }

    /**
     * 월간 통계의 평균 출석 시간을 재설정 합니다.
     *
     * @param monthlyStats  월간 통계
     * @param avgAttendTime 평균 출석 시간
     * @return 월간 통계
     */
    public MonthlyStats setAvgAttendTime(MonthlyStats monthlyStats, LocalTime avgAttendTime) {
        monthlyStats.setAvgAttendTime(avgAttendTime);
        return monthlyStats;
    }

    /**
     * 월간 통계의 평균 퇴실 시간을 재설정 합니다.
     *
     * @param monthlyStats  월간 통계
     * @param avgLeaveTime  평균 퇴실 시간
     * @return 월간 통계
     */
    public MonthlyStats setAvgLeaveTime(MonthlyStats monthlyStats, LocalTime avgLeaveTime) {
        monthlyStats.setAvgLeaveTime(avgLeaveTime);
        return monthlyStats;
    }

}
