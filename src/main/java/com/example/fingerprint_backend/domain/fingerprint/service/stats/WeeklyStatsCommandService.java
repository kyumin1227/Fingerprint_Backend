package com.example.fingerprint_backend.domain.fingerprint.service.stats;

import com.example.fingerprint_backend.domain.fingerprint.entity.WeeklyStats;
import com.example.fingerprint_backend.domain.fingerprint.repository.WeeklyStatsRepository;
import com.example.fingerprint_backend.util.DatePolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class WeeklyStatsCommandService {

    private final WeeklyStatsRepository weeklyStatsRepository;

    /**
     * 주간 통계 생성
     *
     * @param studentNumber 학번
     * @param date          시작 날짜
     * @return 주간 통계
     */
    public WeeklyStats createWeeklyStats(String studentNumber, LocalDate date) {

        LocalDate startDate = DatePolicy.getWeekStartDate(date);

        WeeklyStats weeklyStats = WeeklyStats.builder()
                .studentNumber(studentNumber)
                .startDate(startDate)
                .build();

        return weeklyStatsRepository.save(weeklyStats);
    }


}
