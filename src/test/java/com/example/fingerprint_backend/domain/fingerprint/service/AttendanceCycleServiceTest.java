package com.example.fingerprint_backend.domain.fingerprint.service;

import com.example.fingerprint_backend.TestMemberFactory;
import com.example.fingerprint_backend.domain.fingerprint.entity.AttendanceCycle;
import com.example.fingerprint_backend.dto.GoogleRegisterDto;
import com.example.fingerprint_backend.dto.LoginResponse;
import com.example.fingerprint_backend.entity.MemberEntity;
import com.example.fingerprint_backend.entity.SchoolClass;
import com.example.fingerprint_backend.service.AuthService;
import com.example.fingerprint_backend.service.CleanManagementService;
import com.example.fingerprint_backend.service.LogService;
import com.example.fingerprint_backend.types.MemberRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class AttendanceCycleServiceTest {

    @Autowired
    private AttendanceCycleCommandService attendanceCycleCommandService;
    @Autowired
    private AttendanceCycleQueryService attendanceCycleQueryService;
    @Autowired
    private LogService logService;
    @Autowired
    private CleanManagementService cleanManagementService;

    @Autowired
    private AuthService authService;

    private static LocalDateTime date1;
    private static LocalDateTime date2;
    private static LocalDateTime date3;
    private static LocalDateTime date4;
    private static LocalDateTime date5;

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

        date1 = LocalDateTime.of(2025, 4, 19, 22, 12);
        date2 = LocalDateTime.of(2025, 4, 19, 22, 17);
        date3 = LocalDateTime.of(2025, 4, 19, 22, 22);
        date4 = LocalDateTime.of(2025, 4, 19, 22, 27);
        date5 = LocalDateTime.of(2025, 4, 19, 22, 32);
    }

    @DisplayName("정상적으로 등교 후 하교")
    @Test
    void success1() {
        // given
        attendanceCycleCommandService.create("2423002", date1);

        // when
        attendanceCycleCommandService.close("2423002", date2);

        // then
        AttendanceCycle latestOpenCycle = attendanceCycleQueryService.getLatestCycle("2423002");
        assertThat(latestOpenCycle.getAttendTime()).as("등교 시간").isEqualTo(date1);
        assertThat(latestOpenCycle.getLeaveTime()).as("하교 시간").isEqualTo(date2);
    }

    @DisplayName("등교 후 문 닫음 이후 10분 이내 하교")
    @Test
    void success2() {
        // given
        attendanceCycleCommandService.create("2423002", date1);

        // when
        logService.createClosingTime(date2, "2423007");
        attendanceCycleCommandService.close("2423002", date3);

        // then
        assertThat(attendanceCycleQueryService.getLatestCycle("2423002").getLeaveTime()).isEqualTo(date3);
    }

    @DisplayName("등교 후 문 닫음 이후 재등교 (새 주기 확인)")
    @Test
    void success3() {
        // given
        attendanceCycleCommandService.create("2423002", date1);
        logService.createClosingTime(date2, "2423007");
        attendanceCycleCommandService.close("2423002", date3);

        // when
        attendanceCycleCommandService.create("2423002", date4);

        // then
        AttendanceCycle latestOpenCycle = attendanceCycleQueryService.getLatestCycle("2423002");
        assertThat(latestOpenCycle.getAttendTime()).isEqualTo(date4);
    }

    @DisplayName("등교 후 문 닫기 전 재등교 (이전 등교는 시간 0으로 설정)")
    @Test
    void success4() {

    }

//    @DisplayName("등교 후 문 닫음 이후 재등교")
//    @Test
//    void error1() {
//
//    }

    @DisplayName("등교 후 문 닫음 이후 하교")
    @Test
    void error2() {

    }

    @DisplayName("등교 시간 보다 빠른 시간에 하교")
    @Test
    void error3() {

    }

    @DisplayName("등교 시간 보다 빠른 시간에 재등교")
    @Test
    void error4() {

    }

    @DisplayName("")
    @Test
    void error5() {

    }
}