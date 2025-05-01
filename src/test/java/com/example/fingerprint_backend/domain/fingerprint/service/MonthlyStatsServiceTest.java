package com.example.fingerprint_backend.domain.fingerprint.service;

import com.example.fingerprint_backend.domain.fingerprint.exception.StatsException;
import com.example.fingerprint_backend.domain.fingerprint.service.stats.MonthlyStatsCommandService;
import com.example.fingerprint_backend.domain.fingerprint.service.stats.MonthlyStatsQueryService;
import com.example.fingerprint_backend.domain.fingerprint.util.DatePolicy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class MonthlyStatsServiceTest {

    @Autowired
    private MonthlyStatsCommandService monthlyStatsCommandService;
    @Autowired
    private MonthlyStatsQueryService monthlyStatsQueryService;

    @DisplayName("월간 통계 생성 및 조회")
    @Test
    void success1() {
        // given
        String studentNumber = "2423002";
        LocalDate date = LocalDate.of(2025, 4, 21);

        // when
        monthlyStatsCommandService.createMonthlyStats(studentNumber, date);

        // then
        monthlyStatsQueryService.getMonthlyStatsByStudentNumberAndDate(studentNumber, date)
                .ifPresentOrElse(
                        monthlyStats -> {
                            assertThat(monthlyStats.getEffectiveDate()).as("startDate").isEqualTo(DatePolicy.getMonthStartDate(date));
                            assertThat(monthlyStats.getStudentNumber()).as("studentNumber").isEqualTo(studentNumber);
                        },
                        () -> {
                            throw new StatsException("월간 통계 생성 실패");
                        }
                );
    }

    @DisplayName("월간 통계 다른 날짜로 조회")
    @Test
    void success2() {
        // given
        String studentNumber = "2423002";
        LocalDate date = LocalDate.of(2025, 5, 1);

        // when
        monthlyStatsCommandService.createMonthlyStats(studentNumber, date);

        // then
        monthlyStatsQueryService.getMonthlyStatsByStudentNumberAndDate(studentNumber, date.plusDays(10))
                .ifPresentOrElse(
                        monthlyStats -> {
                            assertThat(monthlyStats.getEffectiveDate()).as("startDate").isEqualTo(DatePolicy.getMonthStartDate(date));
                            assertThat(monthlyStats.getStudentNumber()).as("studentNumber").isEqualTo(studentNumber);
                        },
                        () -> {
                            throw new StatsException("월간 통계 조회 실패");
                        }
                );
    }
}
