package com.example.fingerprint_backend.domain.fingerprint.service.stats;

import com.example.fingerprint_backend.domain.fingerprint.entity.DailyStats;
import com.example.fingerprint_backend.domain.fingerprint.exception.StatsException;
import com.example.fingerprint_backend.util.TimePolicy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest
@Transactional
class DailyStatsServiceTest {

    @Autowired
    private DailyStatsCommandService dailyStatsCommandService;

    private final LocalDateTime dateTime1 = LocalDateTime.of(2025, 4, 21, 20, 6, 0);
    private final LocalDateTime dateTime2 = LocalDateTime.of(2025, 4, 22, 20, 6, 0);
    private final LocalDateTime dateTime3 = LocalDateTime.of(2025, 4, 23, 20, 6, 0);
    private final LocalDateTime dateTime4 = LocalDateTime.of(2025, 4, 30, 20, 6, 0);

    @Autowired
    private DailyStatsQueryService dailyStatsQueryService;

    @BeforeEach
    void setUp() {

    }

    @DisplayName("일일 통계 생성 및 조회")
    @Test
    void success1() {
        // given
        LocalDate date1 = TimePolicy.getLocalDate(dateTime1);

        // when
        dailyStatsCommandService.createDailyStats("2423002", date1);

        // then
        DailyStats dailyStats = dailyStatsQueryService.getDailyStatsByStudentNumberAndDate("2423002", date1);
        assertThat(dailyStats.getEffectiveDate()).as("effectiveDate").isEqualTo(date1);
        assertThat(dailyStats.getStudentNumber()).as("studentNumber").isEqualTo("2423002");
        assertThat(dailyStats.getDayOfWeek()).as("dayOfWeek").isEqualTo(DayOfWeek.MONDAY);
    }

    @DisplayName("일일 통계 조회 - null")
    @Test
    void success2() {
        // given
        LocalDate date1 = TimePolicy.getLocalDate(dateTime1);
        LocalDate date2 = TimePolicy.getLocalDate(dateTime2);

        // when
        dailyStatsCommandService.createDailyStats("2423002", date1);

        // then
        DailyStats dailyStats = dailyStatsQueryService.getDailyStatsByStudentNumberAndDate("2423002", date2);
        assertThat(dailyStats).as("dailyStats").isNull();
    }

    @DisplayName("일일 통계 조회 - 범위")
    @Test
    void success3() {
        // given
        LocalDate date1 = TimePolicy.getLocalDate(dateTime1);
        LocalDate date2 = TimePolicy.getLocalDate(dateTime2);
        LocalDate date3 = TimePolicy.getLocalDate(dateTime3);

        // when
        dailyStatsCommandService.createDailyStats("2423002", date1);
        dailyStatsCommandService.createDailyStats("2423002", date2);
        dailyStatsCommandService.createDailyStats("2423002", date3);

        // then
        assertThat(dailyStatsQueryService.getDailyStatsByStudentNumberAndDateRange("2423002", date1, date3)).as("dailyStats").hasSize(3);
    }

    @DisplayName("일일 통계 체류 시간 업데이트")
    @Test
    void success4() {
        // given
        LocalDate date1 = TimePolicy.getLocalDate(dateTime1);

        // when
        dailyStatsCommandService.createDailyStats("2423002", date1);
        dailyStatsCommandService.updateStayDuration("2423002", date1, 100L);

        // then
        DailyStats dailyStats = dailyStatsQueryService.getDailyStatsByStudentNumberAndDate("2423002", date1);
        assertThat(dailyStats.getStayDuration()).as("stayDuration").isEqualTo(100L);
    }

    @DisplayName("일일 통계 외출 시간 업데이트")
    @Test
    void success5() {
        // given
        LocalDate date1 = TimePolicy.getLocalDate(dateTime1);

        // when
        DailyStats dailyStats = dailyStatsCommandService.createDailyStats("2423002", date1);
        dailyStatsCommandService.updateOutDuration(dailyStats, 100L);

        // then
        DailyStats updateStats = dailyStatsQueryService.getDailyStatsByStudentNumberAndDate("2423002", date1);
        assertThat(updateStats.getOutDuration()).as("outDuration").isEqualTo(100L);

    }

    @DisplayName("존재 하지 않는 통계 업데이트")
    @Test
    void error1() {
        // given
        LocalDate date1 = TimePolicy.getLocalDate(dateTime1);
        LocalDate date2 = TimePolicy.getLocalDate(dateTime2);

        // when
        dailyStatsCommandService.createDailyStats("2423002", date1);

        // then
        assertThatCode(() -> dailyStatsCommandService.updateStayDuration("2423002", date2, 100L))
                .isInstanceOf(StatsException.class)
                .hasMessage("일일 통계를 찾을 수 없습니다.");
    }

    @DisplayName("체류 시간 음수 업데이트")
    @Test
    void error2() {
        // given
        LocalDate date1 = TimePolicy.getLocalDate(dateTime1);

        // when
        dailyStatsCommandService.createDailyStats("2423002", date1);

        // then
        assertThatCode(() -> dailyStatsCommandService.updateStayDuration("2423002", date1, -100L))
                .isInstanceOf(StatsException.class)
                .hasMessage("체류 시간은 음수를 더할 수 없습니다.");
    }
}