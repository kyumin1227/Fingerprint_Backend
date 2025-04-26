package com.example.fingerprint_backend.domain.fingerprint.service.stats;

import com.example.fingerprint_backend.domain.fingerprint.entity.DailyStats;
import com.example.fingerprint_backend.domain.fingerprint.repository.DailyStatsRepository;
import com.example.fingerprint_backend.domain.fingerprint.util.DatePolicy;
import com.example.fingerprint_backend.domain.fingerprint.util.TimePolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
     * 날짜로 일일 통계 리스트를 가져오는 메소드
     *
     * @param date 날짜
     * @return List<DailyStats>
     */
    public List<DailyStats> getDailyStatsByDate(LocalDate date) {
        return dailyStatsRepository.findAllByEffectiveDate(date);
    }

    /**
     * 학번과 날짜로 일일 통계를 가져오는 메소드
     */
    public Optional<DailyStats> getDailyStatsByStudentNumberAndDate(String studentNumber, LocalDate date) {
        return dailyStatsRepository.findByStudentNumberAndEffectiveDate(studentNumber, date);
    }

    /**
     * 학번과 시작날짜, 종료날짜로 일일 통계들을 가져오는 메소드
     */
    public List<DailyStats> getDailyStatsByStudentNumberAndDateRange(String studentNumber, LocalDate startDate, LocalDate endDate) {
        return dailyStatsRepository.findByStudentNumberAndEffectiveDateBetween(studentNumber, startDate, endDate);
    }

    /**
     * 학번과 날짜로 해당 주의 일일 통계들을 가져오는 메소드
     */
    public List<DailyStats> getDailyStatsForWeek(String studentNumber, LocalDate date) {

        LocalDate startDate = DatePolicy.getDateOfWeekDay(date, DayOfWeek.MONDAY);
        LocalDate endDate = DatePolicy.getDateOfWeekDay(date, DayOfWeek.SUNDAY);

        return dailyStatsRepository.findByStudentNumberAndEffectiveDateBetween(studentNumber, startDate, endDate);
    }

    /**
     * 학번과 날짜로 해당 주의 일일 통계들을 가져오는 메소드
     */
    public List<DailyStats> getDailyStatsForWeek(String studentNumber, LocalDateTime dateTime) {

        LocalDate date = TimePolicy.getLocalDate(dateTime);

        return getDailyStatsForWeek(studentNumber, date);
    }

    /**
     * 학번과 날짜로 해당 월의 일일 통계들을 가져오는 메소드
     */
    public List<DailyStats> getDailyStatsForMonth(String studentNumber, LocalDate date) {

        LocalDate startDate = DatePolicy.getMonthStartDate(date);
        LocalDate endDate = DatePolicy.getMonthEndDate(date);

        return dailyStatsRepository.findByStudentNumberAndEffectiveDateBetween(studentNumber, startDate, endDate);
    }

    /**
     * 학번과 날짜로 해당 월의 일일 통계들을 가져오는 메소드
     */
    public List<DailyStats> getDailyStatsForMonth(String studentNumber, LocalDateTime dateTime) {

        LocalDate date = TimePolicy.getLocalDate(dateTime);

        return getDailyStatsForMonth(studentNumber, date);

    }

}
