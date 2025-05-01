package com.example.fingerprint_backend.domain.fingerprint.service.stats;

import com.example.fingerprint_backend.domain.fingerprint.entity.WeeklyStats;
import com.example.fingerprint_backend.domain.fingerprint.repository.WeeklyStatsRepository;
import com.example.fingerprint_backend.domain.fingerprint.util.DatePolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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

        LocalDate weekStartDate = DatePolicy.getDateOfWeekDay(date, DayOfWeek.MONDAY);

        return weeklyStatsRepository.findByStudentNumberAndEffectiveDate(studentNumber, weekStartDate);
    }

    /**
     * 주간 통계 조회
     *
     * @param studentNumber 학번
     * @param dateTime      날짜
     * @return 주간 통계
     */
    public Optional<WeeklyStats> getWeeklyStatsByStudentNumberAndDate(String studentNumber, LocalDateTime dateTime) {

        LocalDate weekStartDate = DatePolicy.getDateOfWeekDay(dateTime, DayOfWeek.MONDAY);

        return getWeeklyStatsByStudentNumberAndDate(studentNumber, weekStartDate);
    }

    /**
     * 날짜로 주간 통계 리스트를 가져오는 메소드
     *
     * @param date 날짜
     * @return List<WeeklyStats>
     */
    public List<WeeklyStats> getWeeklyStatsByDate(LocalDate date) {

        LocalDate weekStartDate = DatePolicy.getDateOfWeekDay(date, DayOfWeek.MONDAY);

        return weeklyStatsRepository.findAllByEffectiveDate(weekStartDate);
    }

    /**
     * 날짜로 체류 시간 기준으로 정렬된 주간 통계 리스트를 가져오는 메소드
     *
     * @param date 날짜
     * @return List<WeeklyStats>
     */
    public List<WeeklyStats> getWeeklyStatsOrderedByStayDuration(LocalDate date) {

        LocalDate weekStartDate = DatePolicy.getDateOfWeekDay(date, DayOfWeek.MONDAY);

        return weeklyStatsRepository.findAllByEffectiveDateOrderByStayDurationDesc(weekStartDate);
    }

    /**
     * 날짜로 등교 시간 기준으로 정렬된 주간 통계 리스트를 가져오는 메소드
     *
     * @param date 날짜
     * @return List<WeeklyStats>
     */
    public List<WeeklyStats> getWeeklyStatsOrderedByAttendanceTime(LocalDate date) {

        LocalDate weekStartDate = DatePolicy.getDateOfWeekDay(date, DayOfWeek.MONDAY);

        return weeklyStatsRepository.findAllByEffectiveDateOrderByAvgAttendTimeAsc(weekStartDate);
    }


}
