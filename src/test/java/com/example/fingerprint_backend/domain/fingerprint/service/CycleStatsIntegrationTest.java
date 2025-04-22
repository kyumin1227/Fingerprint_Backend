package com.example.fingerprint_backend.domain.fingerprint.service;

import com.example.fingerprint_backend.TestMemberFactory;
import com.example.fingerprint_backend.domain.fingerprint.entity.AttendanceCycle;
import com.example.fingerprint_backend.domain.fingerprint.entity.DailyStats;
import com.example.fingerprint_backend.domain.fingerprint.listener.CycleCloseEventHandler;
import com.example.fingerprint_backend.domain.fingerprint.service.cycle.CycleCommandService;
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
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class CycleStatsIntegrationTest {

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
    private CycleCloseEventHandler cycleCloseEventHandler;
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

    @DisplayName("사이클을 통한 통계 생성")
    @Test
    void success1() {
        // given

        // when
        cycleCommandService.createAttendanceCycle("2423002", dateTime1);
        AttendanceCycle attendanceCycle = cycleCommandService.closeAttendanceCycle("2423002", dateTime1.plusHours(2));

//        List<DailyStats> dailyStatsList = statsApplicationService.getOrCreateDailyStatsInRange(attendanceCycle);
//
//        for (DailyStats dailyStats : dailyStatsList) {
//            Long durationTime = statsApplicationService.getDailyDurationTimeForCycle(attendanceCycle, dailyStats.getEffectiveDate());
//            dailyStats.updateStayDuration(durationTime);
//        }

        // then
        DailyStats dailyStats = dailyStatsQueryService.getDailyStatsByStudentNumberAndDate("2423002", TimePolicy.getLocalDate(dateTime1));

        assertThat(dailyStats.getStayDuration()).as("체류 시간").isEqualTo(2 * 60 * 60 * 1000L); // 2시간

    }
}
