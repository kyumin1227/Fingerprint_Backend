package com.example.fingerprint_backend.domain.fingerprint.service.stats;

import com.example.fingerprint_backend.domain.fingerprint.entity.MonthlyStats;
import com.example.fingerprint_backend.domain.fingerprint.repository.MonthlyStatsRepository;
import com.example.fingerprint_backend.domain.fingerprint.util.DatePolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MonthlyStatsQueryService {

    private final MonthlyStatsRepository monthlyStatsRepository;

    /**
     * 월간 통계 조회
     *
     * @param studentNumber 학번
     * @param date          날짜
     * @return 월간 통계
     */
    public Optional<MonthlyStats> getMonthlyStatsByStudentNumberAndDate(String studentNumber, LocalDate date) {

        LocalDate weekStartDate = DatePolicy.getDateOfWeekDay(date, DayOfWeek.MONDAY);

        return monthlyStatsRepository.findByStudentNumberAndStartDate(studentNumber, weekStartDate);
    }

    /**
     * 월간 통계 조회
     *
     * @param studentNumber 학번
     * @param dateTime      날짜
     * @return 월간 통계
     */
    public Optional<MonthlyStats> getMonthlyStatsByStudentNumberAndDate(String studentNumber, LocalDateTime dateTime) {

        LocalDate weekStartDate = DatePolicy.getDateOfWeekDay(dateTime, DayOfWeek.MONDAY);

        return monthlyStatsRepository.findByStudentNumberAndStartDate(studentNumber, weekStartDate);
    }

}
