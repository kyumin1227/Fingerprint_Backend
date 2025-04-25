package com.example.fingerprint_backend.domain.fingerprint.service.cycle;

import com.example.fingerprint_backend.TestMemberFactory;
import com.example.fingerprint_backend.domain.fingerprint.entity.AttendanceCycle;
import com.example.fingerprint_backend.domain.fingerprint.exception.CycleException;
import com.example.fingerprint_backend.dto.GoogleRegisterDto;
import com.example.fingerprint_backend.dto.LoginResponse;
import com.example.fingerprint_backend.entity.MemberEntity;
import com.example.fingerprint_backend.entity.SchoolClass;
import com.example.fingerprint_backend.service.AuthService;
import com.example.fingerprint_backend.service.CleanManagementService;
import com.example.fingerprint_backend.types.LogAction;
import com.example.fingerprint_backend.types.MemberRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest
@Transactional
public class CycleCloseTest {

    @Autowired
    private CleanManagementService cleanManagementService;
    @Autowired
    private AuthService authService;
    @Autowired
    private CycleApplicationService cycleApplicationService;

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

        date1 = LocalDateTime.of(2025, 4, 25, 21, 57);
        date2 = LocalDateTime.of(2025, 4, 25, 22, 2);
        date3 = LocalDateTime.of(2025, 4, 25, 22, 7);
        date4 = LocalDateTime.of(2025, 4, 25, 22, 12);
        date5 = LocalDateTime.of(2025, 4, 25, 22, 17);

    }

    @DisplayName("외출 없이 출석 사이클 종료")
    @Test
    void close1() {
        // given
        cycleApplicationService.createAttendanceCycle("2423002", date1);

        // when
        AttendanceCycle attendanceCycle = cycleApplicationService.closeAttendanceCycle("2423002", date2);

        // then
        assertThat(attendanceCycle.getAttendTime()).as("출석 시간").isEqualTo(date1);
        assertThat(attendanceCycle.getLeaveTime()).as("하교 시간").isEqualTo(date2);

    }

    @DisplayName("외출 없이 출석 사이클 강제 종료")
    @Test
    void close2() {
        // given
        AttendanceCycle attendanceCycle = cycleApplicationService.createAttendanceCycle("2423002", date1);

        // when
        AttendanceCycle forceCloseAttendanceCycle = cycleApplicationService.forceCloseAttendanceCycle(attendanceCycle);

        // then
        assertThat(forceCloseAttendanceCycle.getAttendTime()).as("출석 시간").isEqualTo(date1);
        assertThat(forceCloseAttendanceCycle.getLeaveTime()).as("하교 시간").isEqualTo(date1);

    }

    @DisplayName("외출 복귀 한 채로 출석 사이클 종료")
    @Test
    void close3() {
        // given
        cycleApplicationService.createAttendanceCycle("2423002", date1);
        cycleApplicationService.createOutingCycle("2423002", date2, LogAction.식사);
        cycleApplicationService.closeOutingCycle("2423002", date3);

        // when
        AttendanceCycle attendanceCycle = cycleApplicationService.closeAttendanceCycle("2423002", date4);

        // then
        assertThat(attendanceCycle.getAttendTime()).as("출석 시간").isEqualTo(date1);
        assertThat(attendanceCycle.getLeaveTime()).as("하교 시간").isEqualTo(date4);

    }

    @DisplayName("외출 복귀 하지 않은 채로 출석 사이클 종료")
    @Test
    void close4() {
        // given
        cycleApplicationService.createAttendanceCycle("2423002", date1);
        cycleApplicationService.createOutingCycle("2423002", date2, LogAction.식사);

        // when
        AttendanceCycle attendanceCycle = cycleApplicationService.closeAttendanceCycle("2423002", date4);

        // then
        assertThat(attendanceCycle.getAttendTime()).as("출석 시간").isEqualTo(date1);
        assertThat(attendanceCycle.getLeaveTime()).as("하교 시간").isEqualTo(date4);
        assertThat(attendanceCycle.getOutingCycles().get(0).getOutingStartTime()).as("외출 시작 시간").isEqualTo(date2);
        assertThat(attendanceCycle.getOutingCycles().get(0).getOutingEndTime()).as("외출 종료 시간").isEqualTo(date2);

    }

    @DisplayName("외출 복귀 한 채로 출석 사이클 강제 종료")
    @Test
    void close5() {
        // given
        AttendanceCycle attendanceCycle = cycleApplicationService.createAttendanceCycle("2423002", date1);
        cycleApplicationService.createOutingCycle("2423002", date2, LogAction.식사);
        cycleApplicationService.closeOutingCycle("2423002", date3);

        // when
        AttendanceCycle forceCloseAttendanceCycle = cycleApplicationService.forceCloseAttendanceCycle(attendanceCycle);

        // then
        assertThat(forceCloseAttendanceCycle.getAttendTime()).as("출석 시간").isEqualTo(date1);
        assertThat(forceCloseAttendanceCycle.getLeaveTime()).as("하교 시간").isEqualTo(date3);
        assertThat(forceCloseAttendanceCycle.getOutingCycles().get(0).getOutingStartTime()).as("외출 시작 시간").isEqualTo(date2);
        assertThat(forceCloseAttendanceCycle.getOutingCycles().get(0).getOutingEndTime()).as("외출 종료 시간").isEqualTo(date3);

    }

    @DisplayName("외출 복귀 하지 않은 채로 출석 사이클 강제 종료")
    @Test
    void close6() {
        // given
        AttendanceCycle attendanceCycle = cycleApplicationService.createAttendanceCycle("2423002", date1);
        cycleApplicationService.createOutingCycle("2423002", date2, LogAction.식사);

        // when
        AttendanceCycle forceCloseAttendanceCycle = cycleApplicationService.forceCloseAttendanceCycle(attendanceCycle);

        // then
        assertThat(forceCloseAttendanceCycle.getAttendTime()).as("출석 시간").isEqualTo(date1);
        assertThat(forceCloseAttendanceCycle.getLeaveTime()).as("하교 시간").isEqualTo(date2);
        assertThat(forceCloseAttendanceCycle.getOutingCycles().get(0).getOutingStartTime()).as("외출 시작 시간").isEqualTo(date2);
        assertThat(forceCloseAttendanceCycle.getOutingCycles().get(0).getOutingEndTime()).as("외출 종료 시간").isEqualTo(date2);

    }

    @DisplayName("복수의 외출 후 복귀 한 채로 출석 사이클 종료")
    @Test
    void close7() {
        // given
        cycleApplicationService.createAttendanceCycle("2423002", date1);
        cycleApplicationService.createOutingCycle("2423002", date2, LogAction.식사);
        cycleApplicationService.closeOutingCycle("2423002", date3);
        cycleApplicationService.createOutingCycle("2423002", date4, LogAction.식사);
        cycleApplicationService.closeOutingCycle("2423002", date5);

        // when
        AttendanceCycle attendanceCycle = cycleApplicationService.closeAttendanceCycle("2423002", date5);

        // then
        assertThat(attendanceCycle.getAttendTime()).as("출석 시간").isEqualTo(date1);
        assertThat(attendanceCycle.getLeaveTime()).as("하교 시간").isEqualTo(date5);
        assertThat(attendanceCycle.getOutingCycles().get(0).getOutingStartTime()).as("외출 시작 시간").isEqualTo(date2);
        assertThat(attendanceCycle.getOutingCycles().get(0).getOutingEndTime()).as("외출 종료 시간").isEqualTo(date3);
        assertThat(attendanceCycle.getOutingCycles().get(1).getOutingStartTime()).as("외출 시작 시간").isEqualTo(date4);
        assertThat(attendanceCycle.getOutingCycles().get(1).getOutingEndTime()).as("외출 종료 시간").isEqualTo(date5);

    }

    @DisplayName("복수의 외출 후 복귀 하지 않은 채로 출석 사이클 종료")
    @Test
    void close8() {
        // given
        cycleApplicationService.createAttendanceCycle("2423002", date1);
        cycleApplicationService.createOutingCycle("2423002", date2, LogAction.식사);
        cycleApplicationService.closeOutingCycle("2423002", date3);
        cycleApplicationService.createOutingCycle("2423002", date4, LogAction.식사);

        // when
        AttendanceCycle attendanceCycle = cycleApplicationService.closeAttendanceCycle("2423002", date5);

        // then
        assertThat(attendanceCycle.getAttendTime()).as("출석 시간").isEqualTo(date1);
        assertThat(attendanceCycle.getLeaveTime()).as("하교 시간").isEqualTo(date5);
        assertThat(attendanceCycle.getOutingCycles().get(0).getOutingStartTime()).as("외출 시작 시간").isEqualTo(date2);
        assertThat(attendanceCycle.getOutingCycles().get(0).getOutingEndTime()).as("외출 종료 시간").isEqualTo(date3);
        assertThat(attendanceCycle.getOutingCycles().get(1).getOutingStartTime()).as("외출 시작 시간").isEqualTo(date4);
        assertThat(attendanceCycle.getOutingCycles().get(1).getOutingEndTime()).as("외출 종료 시간").isEqualTo(date4);

    }


    @DisplayName("복수의 외출 후 복귀 한 채로 출석 사이클 강제 종료")
    @Test
    void close9() {
        // given
        AttendanceCycle attendanceCycle = cycleApplicationService.createAttendanceCycle("2423002", date1);
        cycleApplicationService.createOutingCycle("2423002", date2, LogAction.식사);
        cycleApplicationService.closeOutingCycle("2423002", date3);
        cycleApplicationService.createOutingCycle("2423002", date4, LogAction.식사);
        cycleApplicationService.closeOutingCycle("2423002", date5);

        // when
        AttendanceCycle forceCloseAttendanceCycle = cycleApplicationService.forceCloseAttendanceCycle(attendanceCycle);

        // then
        assertThat(forceCloseAttendanceCycle.getAttendTime()).as("출석 시간").isEqualTo(date1);
        assertThat(forceCloseAttendanceCycle.getLeaveTime()).as("하교 시간").isEqualTo(date5);
        assertThat(forceCloseAttendanceCycle.getOutingCycles().get(0).getOutingStartTime()).as("외출 시작 시간").isEqualTo(date2);
        assertThat(forceCloseAttendanceCycle.getOutingCycles().get(0).getOutingEndTime()).as("외출 종료 시간").isEqualTo(date3);
        assertThat(forceCloseAttendanceCycle.getOutingCycles().get(1).getOutingStartTime()).as("외출 시작 시간").isEqualTo(date4);
        assertThat(forceCloseAttendanceCycle.getOutingCycles().get(1).getOutingEndTime()).as("외출 종료 시간").isEqualTo(date5);

    }

    @DisplayName("복수의 외출 후 복귀 하지 않은 채로 출석 사이클 강제 종료")
    @Test
    void close10() {
        // given
        AttendanceCycle attendanceCycle = cycleApplicationService.createAttendanceCycle("2423002", date1);
        cycleApplicationService.createOutingCycle("2423002", date2, LogAction.식사);
        cycleApplicationService.closeOutingCycle("2423002", date3);
        cycleApplicationService.createOutingCycle("2423002", date4, LogAction.식사);

        // when
        AttendanceCycle forceCloseAttendanceCycle = cycleApplicationService.forceCloseAttendanceCycle(attendanceCycle);

        // then
        assertThat(forceCloseAttendanceCycle.getAttendTime()).as("출석 시간").isEqualTo(date1);
        assertThat(forceCloseAttendanceCycle.getLeaveTime()).as("하교 시간").isEqualTo(date4);
        assertThat(forceCloseAttendanceCycle.getOutingCycles().get(0).getOutingStartTime()).as("외출 시작 시간").isEqualTo(date2);
        assertThat(forceCloseAttendanceCycle.getOutingCycles().get(0).getOutingEndTime()).as("외출 종료 시간").isEqualTo(date3);
        assertThat(forceCloseAttendanceCycle.getOutingCycles().get(1).getOutingStartTime()).as("외출 시작 시간").isEqualTo(date4);
        assertThat(forceCloseAttendanceCycle.getOutingCycles().get(1).getOutingEndTime()).as("외출 종료 시간").isEqualTo(date4);

    }

    @DisplayName("하교가 복귀 보다 늦는 경우")
    @Test
    void error1() {
        // given
        cycleApplicationService.createAttendanceCycle("2423002", date1);
        cycleApplicationService.createOutingCycle("2423002", date2, LogAction.식사);
        cycleApplicationService.closeOutingCycle("2423002", date4);

        // when
        assertThatCode(() -> cycleApplicationService.closeAttendanceCycle("2423002", date3))
                .as("하교가 복귀보다 늦는 경우")
                .isInstanceOf(CycleException.class)
                .hasMessage("하교 시간이 복귀 시간보다 늦을 수 없습니다.");

    }

    @DisplayName("이미 종료된 출석 사이클을 다시 종료 시도")
    @Test
    void error2() {
        // given
        cycleApplicationService.createAttendanceCycle("2423002", date1);
        AttendanceCycle attendanceCycle = cycleApplicationService.closeAttendanceCycle("2423002", date2);

        // when
        assertThatCode(() -> cycleApplicationService.forceCloseAttendanceCycle(attendanceCycle))
                .as("이미 종료된 출석 사이클을 다시 종료 시도")
                .isInstanceOf(CycleException.class)
                .hasMessage("이미 하교 시간이 설정되어 있습니다.");
    }

    @DisplayName("외출 종료 시간이 외출 시작 시간 보다 빠른 경우")
    @Test
    void error3() {
        // given
        cycleApplicationService.createAttendanceCycle("2423002", date1);
        cycleApplicationService.createOutingCycle("2423002", date3, LogAction.식사);

        // when
        assertThatCode(() -> cycleApplicationService.closeOutingCycle("2423002", date2))
                .as("외출 종료 시간이 외출 시작 시간 보다 빠른 경우")
                .isInstanceOf(CycleException.class)
                .hasMessage("외출 종료 시간이 외출 시작 시간보다 빠를 수 없습니다.");

    }
}
