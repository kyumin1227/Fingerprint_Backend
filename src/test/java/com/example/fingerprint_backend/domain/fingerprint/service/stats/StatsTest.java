package com.example.fingerprint_backend.domain.fingerprint.service.stats;

import com.example.fingerprint_backend.domain.fingerprint.entity.WeeklyStats;
import com.example.fingerprint_backend.domain.fingerprint.exception.StatsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest
@Transactional
public class StatsTest {

    @DisplayName("주간 통계 생성 성공 - 시작 날짜가 월요일")
    @Test
    void weeklyStatsCreate1() {
        // given
        String studentNumber = "2423002";
        LocalDate date = LocalDate.of(2025, 4, 21); // 월요일

        // when
        WeeklyStats weeklyStats = new WeeklyStats(studentNumber, date);

        // then
        assertThat(weeklyStats.getEffectiveDate()).as("startDate").isEqualTo(date);
        assertThat(weeklyStats.getStudentNumber()).as("studentNumber").isEqualTo(studentNumber);
    }

    @DisplayName("체류 시간 경계값 테스트")
    @Test
    void weeklyStatsCreate2() {
        // given
        String studentNumber = "2423002";
        LocalDate date = LocalDate.of(2025, 4, 21); // 월요일
        long maxStayDuration = 7 * 24 * 60 * 60 * 1000L; // 7일

        // when
        WeeklyStats weeklyStats = new WeeklyStats(studentNumber, date);
        weeklyStats.setStayDuration(maxStayDuration);

        // then
        assertThat(weeklyStats.getEffectiveDate()).as("startDate").isEqualTo(date);
        assertThat(weeklyStats.getStudentNumber()).as("studentNumber").isEqualTo(studentNumber);
    }

    @DisplayName("외출 시간 경계값 테스트")
    @Test
    void weeklyStatsCreate3() {
        // given
        String studentNumber = "2423002";
        LocalDate date = LocalDate.of(2025, 4, 21); // 월요일
        long maxOutDuration = 7 * 24 * 60 * 60 * 1000L; // 7일

        // when
        WeeklyStats weeklyStats = new WeeklyStats(studentNumber, date);
        weeklyStats.setOutDuration(maxOutDuration);

        // then
        assertThat(weeklyStats.getEffectiveDate()).as("startDate").isEqualTo(date);
        assertThat(weeklyStats.getStudentNumber()).as("studentNumber").isEqualTo(studentNumber);
    }

    @DisplayName("주간 통계 생성 실패 - 시작 날짜가 월요일이 아님")
    @Test
    void weeklyStatsError1() {
        // given
        String studentNumber = "2423002";
        LocalDate date = LocalDate.of(2025, 4, 23); // 수요일

        // when
        assertThatCode(() -> new WeeklyStats(studentNumber, date))
                .isInstanceOf(StatsException.class)
                .hasMessageContaining("주간 통계는 월요일부터 시작해야 합니다.");
    }

    @DisplayName("체류 시간 초과 예외")
    @Test
    void weeklyStatsError2() {
        // given
        String studentNumber = "2423002";
        LocalDate date = LocalDate.of(2025, 4, 21); // 월요일
        long stayDuration = 7 * 24 * 60 * 60 * 1000L + 1; // 7일 + 1

        // when
        WeeklyStats weeklyStats = new WeeklyStats(studentNumber, date);
        assertThatCode(() -> weeklyStats.setStayDuration(stayDuration))
                .isInstanceOf(StatsException.class)
                .hasMessageContaining("체류 시간은 7일을 초과할 수 없습니다.");
    }

    @DisplayName("외출 시간 초과 예외")
    @Test
    void weeklyStatsError3() {
        // given
        String studentNumber = "2423002";
        LocalDate date = LocalDate.of(2025, 4, 21); // 월요일
        long outDuration = 7 * 24 * 60 * 60 * 1000L + 1; // 7일 + 1

        // when
        WeeklyStats weeklyStats = new WeeklyStats(studentNumber, date);
        assertThatCode(() -> weeklyStats.setOutDuration(outDuration))
                .isInstanceOf(StatsException.class)
                .hasMessageContaining("외출 시간은 7일을 초과할 수 없습니다.");
    }

    @DisplayName("체류 시간 음수값 예외")
    @Test
    void weeklyStatsError4() {
        // given
        String studentNumber = "2423002";
        LocalDate date = LocalDate.of(2025, 4, 21); // 월요일
        long stayDuration = -7 * 24 * 60 * 60 * 1000L + 1; // 7일 + 1

        // when
        WeeklyStats weeklyStats = new WeeklyStats(studentNumber, date);
        assertThatCode(() -> weeklyStats.setStayDuration(stayDuration))
                .isInstanceOf(StatsException.class)
                .hasMessageContaining("체류 시간은 음수일 수 없습니다.");
    }

}
