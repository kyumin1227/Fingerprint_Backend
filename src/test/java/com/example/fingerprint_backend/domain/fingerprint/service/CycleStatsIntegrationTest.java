package com.example.fingerprint_backend.domain.fingerprint.service;

import com.example.fingerprint_backend.TestMemberFactory;
import com.example.fingerprint_backend.domain.fingerprint.entity.DailyStats;
import com.example.fingerprint_backend.domain.fingerprint.exception.StatsException;
import com.example.fingerprint_backend.domain.fingerprint.listener.AttendanceCycleCloseEventHandler;
import com.example.fingerprint_backend.domain.fingerprint.service.cycle.CycleCommandService;
import com.example.fingerprint_backend.domain.fingerprint.service.cycle.CycleQueryService;
import com.example.fingerprint_backend.domain.fingerprint.service.stats.DailyStatsQueryService;
import com.example.fingerprint_backend.domain.fingerprint.service.stats.StatsApplicationService;
import com.example.fingerprint_backend.dto.GoogleRegisterDto;
import com.example.fingerprint_backend.dto.LoginResponse;
import com.example.fingerprint_backend.entity.MemberEntity;
import com.example.fingerprint_backend.entity.SchoolClass;
import com.example.fingerprint_backend.service.AuthService;
import com.example.fingerprint_backend.service.CleanManagementService;
import com.example.fingerprint_backend.types.MemberRole;
import com.example.fingerprint_backend.util.TimePolicy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest
@Transactional
public class CycleStatsIntegrationTest {

    @Autowired
    private CycleQueryService cycleQueryService;

    //    단일 쓰레드로 비동기 처리
    @TestConfiguration
    static class TestAsyncConfig {
        @Bean(name = "taskExecutor")
        public Executor taskExecutor() {
            return Executors.newSingleThreadExecutor();
        }

    }

    @Autowired
    private CycleCommandService cycleCommandService;

    private final LocalDateTime dateTime1 = LocalDateTime.of(2025, 4, 22, 1, 31, 0);
    @Autowired
    private DailyStatsQueryService dailyStatsQueryService;
    @Autowired
    private CleanManagementService cleanManagementService;
    @Autowired
    private AuthService authService;
    @Autowired
    private AttendanceCycleCloseEventHandler attendanceCycleCloseEventHandler;
    @Autowired
    private StatsApplicationService statsApplicationService;

    @BeforeEach
    void setUp() {
        SchoolClass schoolClass = cleanManagementService.createSchoolClass("2024");

        LoginResponse user1Login = TestMemberFactory.createLoginResponse("2423002", "김규민", null, schoolClass);
        GoogleRegisterDto user1Google = TestMemberFactory.createGoogleRegisterDto("2423002", "김규민");
        MemberEntity user1 = authService.register(user1Login, user1Google);
        user1.setSchoolClass(schoolClass);

        LoginResponse user2Login = TestMemberFactory.createLoginResponse("2423007", "김민정", MemberRole.KEY, schoolClass);
        GoogleRegisterDto user2Google = TestMemberFactory.createGoogleRegisterDto("2423007", "김민정");
        MemberEntity user2 = authService.register(user2Login, user2Google);
        user2.setSchoolClass(schoolClass);
        user2.addRole(MemberRole.KEY);
    }

    @DisplayName("출석 사이클을 통한 통계 생성")
    @Test
    void success1() {
        // given

        // when
        cycleCommandService.createAttendanceCycle("2423002", dateTime1);
        cycleCommandService.closeAttendanceCycle("2423002", dateTime1.plusHours(2));

        // then
        DailyStats dailyStats = dailyStatsQueryService.getDailyStatsByStudentNumberAndDate("2423002", TimePolicy.getLocalDate(dateTime1));

        assertThat(dailyStats.getStayDuration()).as("체류 시간").isEqualTo(2 * 60 * 60 * 1000L); // 2시간

    }

    @DisplayName("출석 사이클을 통한 통계 생성 (3일)")
    @Test
    void success2() {
        // given
        LocalDateTime dateTime = LocalDateTime.of(2025, 4, 22, 15, 0, 0);

        // when
        cycleCommandService.createAttendanceCycle("2423002", dateTime);
        cycleCommandService.closeAttendanceCycle("2423002", dateTime.plusDays(2).plusHours(3)); // 51시간

        // then
        DailyStats dailyStats1 = dailyStatsQueryService.getDailyStatsByStudentNumberAndDate("2423002", TimePolicy.getLocalDate(dateTime));
        DailyStats dailyStats2 = dailyStatsQueryService.getDailyStatsByStudentNumberAndDate("2423002", TimePolicy.getLocalDate(dateTime.plusDays(1)));
        DailyStats dailyStats3 = dailyStatsQueryService.getDailyStatsByStudentNumberAndDate("2423002", TimePolicy.getLocalDate(dateTime.plusDays(2)));

        assertThat(dailyStats1.getStayDuration()).as("1일 체류 시간").isEqualTo(15 * 60 * 60 * 1000L); // 15시간
        assertThat(dailyStats2.getStayDuration()).as("2일 체류 시간").isEqualTo(24 * 60 * 60 * 1000L); // 24시간
        assertThat(dailyStats3.getStayDuration()).as("3일 체류 시간").isEqualTo(12 * 60 * 60 * 1000L); // 12시간
    }

    @DisplayName("체류 시간 초과 예외 (경계값)")
    @Test
    void error1() {
        // given
        LocalDateTime dateTime = LocalDateTime.of(2025, 4, 22, 18, 0, 0);

        // when
        cycleCommandService.createAttendanceCycle("2423002", dateTime);
        cycleCommandService.closeAttendanceCycle("2423002", dateTime.plusHours(18));

        // then
        DailyStats dailyStats1 = dailyStatsQueryService.getDailyStatsByStudentNumberAndDate("2423002", TimePolicy.getLocalDate(dateTime));

        assertThatCode(() -> dailyStats1.updateStayDuration(12 * 60 * 60 * 1000L + 1)) // 1ms 초과
                .as("체류 시간 초과 예외")
                .isInstanceOf(StatsException.class)
                .hasMessage("체류 시간은 24시간을 초과할 수 없습니다.");
    }

    @DisplayName("체류 시간 음수값 예외")
    @Test
    void error2() {
        // given
        LocalDateTime dateTime = LocalDateTime.of(2025, 4, 22, 19, 37, 0);

        // when
        cycleCommandService.createAttendanceCycle("2423002", dateTime);
        cycleCommandService.closeAttendanceCycle("2423002", dateTime.plusHours(18));

        // then
        DailyStats dailyStats1 = dailyStatsQueryService.getDailyStatsByStudentNumberAndDate("2423002", TimePolicy.getLocalDate(dateTime));

        assertThatCode(() -> dailyStats1.updateStayDuration(-10 * 60 * 60 * 1000L))
                .as("체류 시간 음수값 예외")
                .isInstanceOf(StatsException.class)
                .hasMessage("체류 시간은 음수를 더할 수 없습니다.");
    }
}
