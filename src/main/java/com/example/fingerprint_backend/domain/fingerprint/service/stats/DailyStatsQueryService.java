package com.example.fingerprint_backend.domain.fingerprint.service.stats;

import com.example.fingerprint_backend.domain.fingerprint.entity.DailyStats;
import com.example.fingerprint_backend.domain.fingerprint.repository.DailyStatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DailyStatsQueryService {

    private final DailyStatsRepository dailyStatsRepository;

    /**
     * 학번으로 일일 통계를 가져오는 메소드
     *
     * @param studentNumber 학번
     * @return DailyStats
     */
    public DailyStats getDailyStatsByStudentNumber(String studentNumber) {
        return dailyStatsRepository.findByStudentNumber(studentNumber)
                .orElse(null);
    }

    /**
     * 날짜로 일일 통계를 가져오는 메소드
     *
     * @param date 날짜
     * @return DailyStats
     */
    public DailyStats getDailyStatsByDate(LocalDate date) {
        return dailyStatsRepository.findByEffectiveDate(date)
                .orElse(null);
    }

    /**
     * 학번과 날짜로 일일 통계를 가져오는 메소드
     */
    public DailyStats getDailyStatsByStudentNumberAndDate(String studentNumber, LocalDate date) {
        return dailyStatsRepository.findByStudentNumberAndEffectiveDate(studentNumber, date)
                .orElse(null);
    }

    /**
     * 학번과 시작날짜, 종료날짜로 일일 통계들을 가져오는 메소드
     */
    public List<DailyStats> getDailyStatsByStudentNumberAndDateRange(String studentNumber, LocalDate startDate, LocalDate endDate) {
        return dailyStatsRepository.findByStudentNumberAndEffectiveDateBetween(studentNumber, startDate, endDate);
    }

}
