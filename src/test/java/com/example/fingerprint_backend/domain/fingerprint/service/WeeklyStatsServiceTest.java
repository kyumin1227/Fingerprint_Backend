package com.example.fingerprint_backend.domain.fingerprint.service;

import com.example.fingerprint_backend.domain.fingerprint.entity.WeeklyStats;
import com.example.fingerprint_backend.domain.fingerprint.exception.StatsException;
import com.example.fingerprint_backend.domain.fingerprint.service.stats.WeeklyStatsCommandService;
import com.example.fingerprint_backend.domain.fingerprint.service.stats.WeeklyStatsQueryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

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
                            assertThat(weeklyStats.getStartDate()).as("startDate").isEqualTo(date);
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
                            assertThat(weeklyStats.getStartDate()).as("startDate").isEqualTo(date);
                            assertThat(weeklyStats.getStudentNumber()).as("studentNumber").isEqualTo(studentNumber);
                        },
                        () -> {
                            throw new StatsException("주간 통계 조회 실패");
                        }
                );
    }


}
