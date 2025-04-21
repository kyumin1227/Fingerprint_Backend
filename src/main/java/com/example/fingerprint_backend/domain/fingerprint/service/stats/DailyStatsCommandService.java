package com.example.fingerprint_backend.domain.fingerprint.service.stats;

import com.example.fingerprint_backend.domain.fingerprint.entity.DailyStats;
import com.example.fingerprint_backend.domain.fingerprint.exception.StatsException;
import com.example.fingerprint_backend.domain.fingerprint.repository.DailyStatsRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DailyStatsCommandService {

    private final DailyStatsRepository dailyStatsRepository;
    private final DailyStatsQueryService dailyStatsQueryService;

    /**
     * 일일 통계 생성
     *
     * @param studentNumber 학번
     * @param effectiveDate 날짜
     * @return 일일 통계
     */
    public DailyStats createDailyStats(String studentNumber, LocalDate effectiveDate) {

        DailyStats dailyStats = DailyStats.builder()
                .studentNumber(studentNumber)
                .effectiveDate(effectiveDate)
                .build();

        return dailyStatsRepository.save(dailyStats);
    }

    /**
     * 일일 통계의 체류 시간 업데이트
     *
     * @param studentNumber 학번
     * @param effectiveDate 날짜
     * @param stayDuration  체류 시간
     * @return 일일 통계
     */
    public DailyStats updateStayDuration(String studentNumber, LocalDate effectiveDate, Long stayDuration) {

        DailyStats dailyStats = dailyStatsQueryService.getDailyStatsByStudentNumberAndDate(studentNumber,
                effectiveDate);

        if (dailyStats == null) {
            throw new StatsException("일일 통계를 찾을 수 없습니다.");
        }

        dailyStats.updateStayDuration(stayDuration);

        return dailyStats;
    }

    /**
     * 일일 통계의 체류 시간 업데이트
     *
     * @param dailyStats   일일 통계
     * @param stayDuration 체류 시간
     * @return 일일 통계
     */
    public DailyStats updateStayDuration(DailyStats dailyStats, Long stayDuration) {

        if (dailyStats == null) {
            throw new StatsException("일일 통계를 찾을 수 없습니다.");
        }

        dailyStats.updateStayDuration(stayDuration);

        return dailyStats;
    }

    /**
     * 일일 통계의 외출 시간 업데이트
     *
     * @param studentNumber  학번
     * @param effectiveDate  날짜
     * @param outingDuration 외출 시간
     * @return 일일 통계
     */
    public DailyStats updateOutDuration(String studentNumber, LocalDate effectiveDate, Long outingDuration) {
        DailyStats dailyStats = dailyStatsQueryService.getDailyStatsByStudentNumberAndDate(studentNumber,
                effectiveDate);

        if (dailyStats == null) {
            throw new StatsException("일일 통계를 찾을 수 없습니다.");
        }

        dailyStats.updateOutDuration(outingDuration);
        return dailyStats;
    }

    /**
     * 일일 통계의 외출 시간 업데이트
     *
     * @param dailyStats  일일 통계
     * @param outDuration 외출 시간
     * @return 일일 통계
     */
    public DailyStats updateOutDuration(DailyStats dailyStats, Long outDuration) {
        if (dailyStats == null) {
            throw new StatsException("일일 통계를 찾을 수 없습니다.");
        }

        dailyStats.updateOutDuration(outDuration);
        return dailyStats;
    }

}
