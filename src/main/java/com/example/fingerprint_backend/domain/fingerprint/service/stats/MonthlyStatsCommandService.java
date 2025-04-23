package com.example.fingerprint_backend.domain.fingerprint.service.stats;

import com.example.fingerprint_backend.domain.fingerprint.entity.MonthlyStats;
import com.example.fingerprint_backend.domain.fingerprint.entity.WeeklyStats;
import com.example.fingerprint_backend.domain.fingerprint.repository.MonthlyStatsRepository;
import com.example.fingerprint_backend.util.DatePolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional
@RequiredArgsConstructor
public class MonthlyStatsCommandService {

    private final MonthlyStatsRepository monthlyStatsRepository;

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

}
