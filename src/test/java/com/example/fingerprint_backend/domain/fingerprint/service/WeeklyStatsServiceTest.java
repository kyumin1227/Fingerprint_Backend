package com.example.fingerprint_backend.domain.fingerprint.service;

import com.example.fingerprint_backend.domain.fingerprint.exception.StatsException;
import com.example.fingerprint_backend.domain.fingerprint.service.stats.WeeklyStatsCommandService;
import com.example.fingerprint_backend.domain.fingerprint.service.stats.WeeklyStatsQueryService;
import com.example.fingerprint_backend.domain.fingerprint.util.DatePolicy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class WeeklyStatsServiceTest {

    @Autowired
    private WeeklyStatsCommandService weeklyStatsCommandService;
    @Autowired
    private WeeklyStatsQueryService weeklyStatsQueryService;

    @DisplayName("주간 통계 생성 및 조회")
    @Test
    void success1() {
        // given
        String studentNumber = "2423002";
        LocalDate date = LocalDate.of(2025, 4, 21);

        // when
        weeklyStatsCommandService.createWeeklyStats(studentNumber, date);

        // then
        weeklyStatsQueryService.getWeeklyStatsByStudentNumberAndDate(studentNumber, date)
                .ifPresentOrElse(
                        weeklyStats -> {
                            assertThat(weeklyStats.getEffectiveDate()).as("startDate").isEqualTo(DatePolicy.getDateOfWeekDay(date, DayOfWeek.MONDAY));
                            System.out.println("weeklyStats.getEffectiveDate() = " + weeklyStats.getEffectiveDate());
                            assertThat(weeklyStats.getStudentNumber()).as("studentNumber").isEqualTo(studentNumber);
                        },
                        () -> {
                            throw new StatsException("주간 통계 생성 실패");
                        }
                );
    }

    @DisplayName("주간 통계 다른 날짜로 조회")
    @Test
    void success2() {
        // given
        String studentNumber = "2423002";
        LocalDate date = LocalDate.of(2025, 4, 21);

        // when
        weeklyStatsCommandService.createWeeklyStats(studentNumber, date);

        // then
        weeklyStatsQueryService.getWeeklyStatsByStudentNumberAndDate(studentNumber, date.plusDays(3))
                .ifPresentOrElse(
                        weeklyStats -> {
                            assertThat(weeklyStats.getEffectiveDate()).as("startDate").isEqualTo(date);
                            assertThat(weeklyStats.getStudentNumber()).as("studentNumber").isEqualTo(studentNumber);
                        },
                        () -> {
                            throw new StatsException("주간 통계 조회 실패");
                        }
                );
    }

    @DisplayName("월요일이 아닌 다른 날짜로 생성")
    @Test
    void success3() {
        // given
        String studentNumber = "2423002";
        LocalDate date = LocalDate.of(2025, 5, 1);

        // when
        weeklyStatsCommandService.createWeeklyStats(studentNumber, date);

        // then
        weeklyStatsQueryService.getWeeklyStatsByStudentNumberAndDate(studentNumber, date)
                .ifPresentOrElse(
                        weeklyStats -> {
                            assertThat(weeklyStats.getEffectiveDate()).as("startDate").isEqualTo(DatePolicy.getDateOfWeekDay(date, DayOfWeek.MONDAY));
                            assertThat(weeklyStats.getStudentNumber()).as("studentNumber").isEqualTo(studentNumber);
                        },
                        () -> {
                            throw new StatsException("주간 통계 조회 실패");
                        }
                );

    }
}
