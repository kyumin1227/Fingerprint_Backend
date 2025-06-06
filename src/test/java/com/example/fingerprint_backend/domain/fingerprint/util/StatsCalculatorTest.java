package com.example.fingerprint_backend.domain.fingerprint.util;

import com.example.fingerprint_backend.domain.fingerprint.entity.AttendanceCycle;
import com.example.fingerprint_backend.domain.fingerprint.entity.DailyStats;
import com.example.fingerprint_backend.domain.fingerprint.entity.LogEntity;
import com.example.fingerprint_backend.domain.fingerprint.service.cycle.AttendanceCycleCommandService;
import com.example.fingerprint_backend.domain.fingerprint.types.LogAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class StatsCalculatorTest {

    @Autowired
    private AttendanceCycleCommandService attendanceCycleCommandService;

    private LocalDateTime date1;
    private LocalDateTime date2;
    private String studentNumber1;

    @BeforeEach
    void setUp() {

        date1 = LocalDateTime.of(2025, 4, 26, 22, 27);
        date2 = LocalDateTime.of(2025, 4, 26, 22, 37);
        studentNumber1 = "2423002";
    }

    @DisplayName("사이클의 체류 시간 0인 경우")
    @Test
    void getDailyDurationTimeForCycle1() {
        // given
        AttendanceCycle attendanceCycle = attendanceCycleCommandService.createAttendanceCycle(studentNumber1, date1);
        attendanceCycle.setLeaveTime(date1);

        // when
        Long timeForCycle = StatsCalculator.getDailyDurationTimeForCycle(attendanceCycle, TimePolicy.getLocalDate(date1));

        // then
        assertThat(timeForCycle).isEqualTo(0L);

    }

    @DisplayName("사이클의 체류 시간이 있는 경우")
    @Test
    void getDailyDurationTimeForCycle2() {
        // given
        AttendanceCycle attendanceCycle = attendanceCycleCommandService.createAttendanceCycle(studentNumber1, date1);
        attendanceCycle.setLeaveTime(date2);

        // when
        Long timeForCycle = StatsCalculator.getDailyDurationTimeForCycle(attendanceCycle, TimePolicy.getLocalDate(date1));

        // then
        assertThat(timeForCycle).isEqualTo(10 * 60 * 1000L);

    }

    @DisplayName("사이클의 체류 시간이 이틀에 걸친 경우")
    @Test
    void getDailyDurationTimeForCycle3() {
        // given
        AttendanceCycle attendanceCycle = attendanceCycleCommandService.createAttendanceCycle(studentNumber1, date1);
        attendanceCycle.setLeaveTime(date1.plusDays(1));

        // when
        Long timeForCycle1 = StatsCalculator.getDailyDurationTimeForCycle(attendanceCycle, TimePolicy.getLocalDate(date1));
        Long timeForCycle2 = StatsCalculator.getDailyDurationTimeForCycle(attendanceCycle, TimePolicy.getLocalDate(date1.plusDays(1)));

        // then
        assertThat(timeForCycle1).as("date1").isEqualTo(7 * 60 * 60 * 1000L + 33 * 60 * 1000L);
        assertThat(timeForCycle2).as("date2").isEqualTo(16 * 60 * 60 * 1000L + 27 * 60 * 1000L);

    }

    @DisplayName("사이클의 체류 시간이 수일에 걸친 경우")
    @Test
    void getDailyDurationTimeForCycle4() {
        // given
        AttendanceCycle attendanceCycle = attendanceCycleCommandService.createAttendanceCycle(studentNumber1, date1);
        attendanceCycle.setLeaveTime(date1.plusDays(3));

        // when
        Long timeForCycle1 = StatsCalculator.getDailyDurationTimeForCycle(attendanceCycle, TimePolicy.getLocalDate(date1));
        Long timeForCycle2 = StatsCalculator.getDailyDurationTimeForCycle(attendanceCycle, TimePolicy.getLocalDate(date1.plusDays(1)));
        Long timeForCycle3 = StatsCalculator.getDailyDurationTimeForCycle(attendanceCycle, TimePolicy.getLocalDate(date1.plusDays(2)));
        Long timeForCycle4 = StatsCalculator.getDailyDurationTimeForCycle(attendanceCycle, TimePolicy.getLocalDate(date1.plusDays(3)));
        Long timeForCycle5 = StatsCalculator.getDailyDurationTimeForCycle(attendanceCycle, TimePolicy.getLocalDate(date1.plusDays(4)));

        // then
        assertThat(timeForCycle1).as("date1").isEqualTo(7 * 60 * 60 * 1000L + 33 * 60 * 1000L);
        assertThat(timeForCycle2).as("date2").isEqualTo(24 * 60 * 60 * 1000L);
        assertThat(timeForCycle3).as("date3").isEqualTo(24 * 60 * 60 * 1000L);
        assertThat(timeForCycle4).as("date4").isEqualTo(16 * 60 * 60 * 1000L + 27 * 60 * 1000L);
        assertThat(timeForCycle5).as("date5").isEqualTo(0L);
    }

    @DisplayName("일간 통계의 체류시간이 0인 경우")
    @Test
    void getTotalStayDuration1() {
        // given
        DailyStats dailyStats = new DailyStats(studentNumber1, TimePolicy.getLocalDate(date1));
        dailyStats.updateStayDuration(0L);

        // when
        Long totalStayDuration = StatsCalculator.getTotalStayDuration(List.of(dailyStats));

        // then
        assertThat(totalStayDuration).isEqualTo(0L);

    }

    @DisplayName("복수의 일간 통계의 체류시간이 0인 경우")
    @Test
    void getTotalStayDuration2() {
        // given
        DailyStats dailyStats1 = new DailyStats(studentNumber1, TimePolicy.getLocalDate(date1));
        dailyStats1.updateStayDuration(0L);
        DailyStats dailyStats2 = new DailyStats(studentNumber1, TimePolicy.getLocalDate(date2));
        dailyStats2.updateStayDuration(0L);

        // when
        Long totalStayDuration = StatsCalculator.getTotalStayDuration(List.of(dailyStats1, dailyStats2));

        // then
        assertThat(totalStayDuration).isEqualTo(0L);
    }

    @DisplayName("일간 통계의 체류시간이 있는 경우")
    @Test
    void getTotalStayDuration3() {
        // given
        DailyStats dailyStats1 = new DailyStats(studentNumber1, TimePolicy.getLocalDate(date1));
        dailyStats1.updateStayDuration(10L);

        // when
        Long totalStayDuration = StatsCalculator.getTotalStayDuration(List.of(dailyStats1));

        // then
        assertThat(totalStayDuration).isEqualTo(10L);

    }

    @DisplayName("복수의 일간 통계의 체류시간이 있는 경우")
    @Test
    void getTotalStayDuration4() {
        // given
        DailyStats dailyStats1 = new DailyStats(studentNumber1, TimePolicy.getLocalDate(date1));
        dailyStats1.updateStayDuration(100L);
        DailyStats dailyStats2 = new DailyStats(studentNumber1, TimePolicy.getLocalDate(date2));
        dailyStats2.updateStayDuration(200L);

        // when
        Long totalStayDuration = StatsCalculator.getTotalStayDuration(List.of(dailyStats1, dailyStats2));

        // then
        assertThat(totalStayDuration).isEqualTo(300L);

    }

    @DisplayName("로그 엔티티가 없는 경우")
    @Test
    void getAvgTime1() {
        // given

        // when
        LocalTime avgTime = StatsCalculator.getAvgTime(List.of());

        // then
        assertThat(avgTime).isEqualTo(LocalTime.of(0, 0, 0));

    }

    @DisplayName("로그 엔티티가 하나 있는 경우")
    @Test
    void getAvgTime2() {
        // given
        LogEntity logEntity = new LogEntity(studentNumber1, date1, LogAction.등교);

        // when
        LocalTime avgTime = StatsCalculator.getAvgTime(List.of(logEntity));

        // then
        assertThat(avgTime).isEqualTo(LocalTime.of(22, 27, 0));

    }

    @DisplayName("같은 시간의 로그 엔티티가 여러개 있는 경우")
    @Test
    void getAvgTime3() {
        // given
        LogEntity logEntity1 = new LogEntity(studentNumber1, date1, LogAction.등교);
        LogEntity logEntity2 = new LogEntity(studentNumber1, date1, LogAction.등교);
        LogEntity logEntity3 = new LogEntity(studentNumber1, date1, LogAction.등교);

        // when
        LocalTime avgTime = StatsCalculator.getAvgTime(List.of(logEntity1, logEntity2, logEntity3));

        // then
        assertThat(avgTime).isEqualTo(LocalTime.of(22, 27, 0));

    }

    @DisplayName("서로 다른 시간의 로그 엔티티가 있는 경우")
    @Test
    void getAvgTime4() {
        // given
        LogEntity logEntity1 = new LogEntity(studentNumber1, date1.plusHours(1), LogAction.등교);
        LogEntity logEntity2 = new LogEntity(studentNumber1, date1.minusHours(1), LogAction.등교);

        // when
        LocalTime avgTime = StatsCalculator.getAvgTime(List.of(logEntity1, logEntity2));

        // then
        assertThat(avgTime).isEqualTo(LocalTime.of(22, 27, 0));

    }

    @DisplayName("서로 다른 시간의 로그 엔티티가 여러개 있는 경우 2")
    @Test
    void getAvgTime5() {
        // given
        LogEntity logEntity1 = new LogEntity(studentNumber1, date1.plusHours(1).minusMinutes(15), LogAction.등교);
        LogEntity logEntity2 = new LogEntity(studentNumber1, date1.minusHours(1).plusMinutes(15), LogAction.등교);
        LogEntity logEntity3 = new LogEntity(studentNumber1, date1.plusHours(2).minusSeconds(40), LogAction.등교);
        LogEntity logEntity4 = new LogEntity(studentNumber1, date1.minusHours(2).plusSeconds(40), LogAction.등교);

        // when
        LocalTime avgTime = StatsCalculator.getAvgTime(List.of(logEntity1, logEntity2, logEntity3, logEntity4));

        // then
        assertThat(avgTime).isEqualTo(LocalTime.of(22, 27, 0));

    }

}