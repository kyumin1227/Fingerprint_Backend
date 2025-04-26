package com.example.fingerprint_backend.domain.fingerprint.service.stats;

import com.example.fingerprint_backend.domain.fingerprint.entity.MonthlyStats;
import com.example.fingerprint_backend.domain.fingerprint.repository.MonthlyStatsRepository;
import com.example.fingerprint_backend.domain.fingerprint.util.DatePolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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

        return monthlyStatsRepository.findByStudentNumberAndEffectiveDate(studentNumber, date);
    }

    /**
     * 월간 통계 조회
     *
     * @param studentNumber 학번
     * @param dateTime      날짜
     * @return 월간 통계
     */
    public Optional<MonthlyStats> getMonthlyStatsByStudentNumberAndDate(String studentNumber, LocalDateTime dateTime) {

        LocalDate monthStartDate = DatePolicy.getMonthStartDate(dateTime);

        return getMonthlyStatsByStudentNumberAndDate(studentNumber, monthStartDate);
    }

    /**
     * 날짜로 월간 통계 리스트를 가져오는 메소드
     *
     * @param date 날짜
     * @return List<MonthlyStats>
     */
    public List<MonthlyStats> getMonthlyStatsByDate(LocalDate date) {

        LocalDate monthStartDate = DatePolicy.getMonthStartDate(date);

        return monthlyStatsRepository.findAllByEffectiveDate(monthStartDate);
    }

    /**
     * 날짜로 체류 시간 기준으로 정렬된 월간 통계 리스트를 가져오는 메소드
     *
     * @param date 날짜
     * @return List<MonthlyStats>
     */
    public List<MonthlyStats> getMonthlyStatsOrderedByStayDuration(LocalDate date) {

        LocalDate monthStartDate = DatePolicy.getMonthStartDate(date);

        return monthlyStatsRepository.findAllByEffectiveDateOrderByStayDurationDesc(monthStartDate);
    }

    /**
     * 날짜로 등교 시간 기준으로 정렬된 월간 통계 리스트를 가져오는 메소드
     *
     * @param date 날짜
     * @return List<MonthlyStats>
     */
    public List<MonthlyStats> getMonthlyStatsOrderedByAttendanceTime(LocalDate date) {

        LocalDate monthStartDate = DatePolicy.getMonthStartDate(date);

        return monthlyStatsRepository.findAllByEffectiveDateOrderByAvgAttendTimeAsc(monthStartDate);
    }

}
