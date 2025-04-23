package com.example.fingerprint_backend.domain.fingerprint.service.stats;

import com.example.fingerprint_backend.domain.fingerprint.entity.WeeklyStats;
import com.example.fingerprint_backend.domain.fingerprint.repository.WeeklyStatsRepository;
import com.example.fingerprint_backend.util.DatePolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WeeklyStatsQueryService {

    private final WeeklyStatsRepository weeklyStatsRepository;

    /**
     * 주간 통계 조회
     *
     * @param studentNumber 학번
     * @param date          날짜
     * @return 주간 통계
     */
    public Optional<WeeklyStats> getWeeklyStatsByStudentNumberAndDate(String studentNumber, LocalDate date) {

        LocalDate weekStartDate = DatePolicy.getWeekStartDate(date);

        return weeklyStatsRepository.findByStudentNumberAndStartDate(studentNumber, weekStartDate);
    }

    /**
     * 주간 통계 조회
     *
     * @param studentNumber 학번
     * @param dateTime      날짜
     * @return 주간 통계
     */
    public Optional<WeeklyStats> getWeeklyStatsByStudentNumberAndDate(String studentNumber, LocalDateTime dateTime) {

        LocalDate weekStartDate = DatePolicy.getWeekStartDate(dateTime);

        return weeklyStatsRepository.findByStudentNumberAndStartDate(studentNumber, weekStartDate);
    }
}
