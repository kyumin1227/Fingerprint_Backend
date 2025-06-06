package com.example.fingerprint_backend.domain.fingerprint.service.cycle;

import com.example.fingerprint_backend.TestMemberFactory;
import com.example.fingerprint_backend.domain.fingerprint.entity.AttendanceCycle;
import com.example.fingerprint_backend.domain.fingerprint.entity.OutingCycle;
import com.example.fingerprint_backend.domain.fingerprint.exception.CycleException;
import com.example.fingerprint_backend.domain.fingerprint.service.classClosingTime.ClassClosingTimeApplicationService;
import com.example.fingerprint_backend.domain.fingerprint.service.log.LogService;
import com.example.fingerprint_backend.dto.GoogleRegisterDto;
import com.example.fingerprint_backend.dto.LoginResponse;
import com.example.fingerprint_backend.entity.MemberEntity;
import com.example.fingerprint_backend.entity.SchoolClass;
import com.example.fingerprint_backend.service.AuthService;
import com.example.fingerprint_backend.service.CleanManagementService;
import com.example.fingerprint_backend.domain.fingerprint.types.LogAction;
import com.example.fingerprint_backend.types.MemberRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest
@Transactional
class CycleCreateTest {

    @Autowired
    private AttendanceCycleQueryService attendanceCycleQueryService;
    @Autowired
    private LogService logService;
    @Autowired
    private CleanManagementService cleanManagementService;
    @Autowired
    private AuthService authService;
    @Autowired
    private OutingCycleCommandService outingCycleCommandService;
    @Autowired
    private OutingCycleQueryService outingCycleQueryService;
    @Autowired
    private CycleApplicationService cycleApplicationService;

    private static LocalDateTime date1;
    private static LocalDateTime date2;
    private static LocalDateTime date3;
    private static LocalDateTime date4;
    private static LocalDateTime date5;
    @Autowired
    private ClassClosingTimeApplicationService classClosingTimeApplicationService;

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
    @DirtiesContext
    void success1() {
        // given
        cycleApplicationService.createAttendanceCycle("2423002", date1);

        // when
        cycleApplicationService.closeAttendanceCycle("2423002", date2);

        // then
        AttendanceCycle latestOpenCycle = attendanceCycleQueryService.getLatestCycle("2423002");
        assertThat(latestOpenCycle.getAttendTime()).as("등교 시간").isEqualTo(date1);
        assertThat(latestOpenCycle.getLeaveTime()).as("하교 시간").isEqualTo(date2);
    }

    @DisplayName("등교 후 문 닫음 이후 10분 이내 하교")
    @Test
    void success2() {
        // given
        cycleApplicationService.createAttendanceCycle("2423002", date1);

        // when
        classClosingTimeApplicationService.createClosingTime(date2, "2423007");
        cycleApplicationService.closeAttendanceCycle("2423002", date3);

        // then
        assertThat(attendanceCycleQueryService.getLatestCycle("2423002").getLeaveTime()).isEqualTo(date3);
    }

    @DisplayName("등교 후 문 닫음 이후 재등교 (새 주기 확인)")
    @Test
    void success3() {
        // given
        cycleApplicationService.createAttendanceCycle("2423002", date1);
        classClosingTimeApplicationService.createClosingTime(date2, "2423007");
        cycleApplicationService.closeAttendanceCycle("2423002", date3);

        // when
        cycleApplicationService.createAttendanceCycle("2423002", date4);

        // then
        AttendanceCycle latestOpenCycle = attendanceCycleQueryService.getLatestCycle("2423002");
        assertThat(latestOpenCycle.getAttendTime()).isEqualTo(date4);
    }

    @DisplayName("등교 후 문 닫기 전 재등교 (이전 체류 시간은 0으로 설정)")
    @Test
    void success4() {
        // given
        cycleApplicationService.createAttendanceCycle("2423002", date1);
        AttendanceCycle firstCycle = attendanceCycleQueryService.getLatestCycle("2423002");

        // when
        cycleApplicationService.createAttendanceCycle("2423002", date3);
        AttendanceCycle secondCycle = attendanceCycleQueryService.getLatestCycle("2423002");

        // then
        assertThat(firstCycle.getLeaveTime()).isEqualTo(date1);
        assertThat(secondCycle.getAttendTime()).isEqualTo(date3);
        assertThat(secondCycle.getLeaveTime()).isNull();

    }

    @DisplayName("이전 등교 시간 보다 빠른 시간에 재등교 (이전 등교한 체류 시간은 0으로 설정)")
    @Test
    void success5() {

        // given
        cycleApplicationService.createAttendanceCycle("2423002", date1);
        AttendanceCycle firstCycle = attendanceCycleQueryService.getLatestCycle("2423002");

        // when
        cycleApplicationService.createAttendanceCycle("2423002", date1.minusMinutes(1));
        AttendanceCycle secondCycle = attendanceCycleQueryService.getLatestOpenCycle("2423002");

        // then
        assertThat(firstCycle.getLeaveTime()).as("첫 사이클 하교 시간").isEqualTo(date1);
        assertThat(secondCycle.getAttendTime()).as("두번째 사이클 등교 시간").isEqualTo(date1.minusMinutes(1));
        assertThat(secondCycle.getLeaveTime()).as("두번째 사이클 하교 시간").isNull();

    }

    @DisplayName("없는 학번으로 조회")
    @Test
    void success6() {
        // given
        cycleApplicationService.createAttendanceCycle("2423002", date1);

        // when
        AttendanceCycle latestOpenCycle = attendanceCycleQueryService.getLatestOpenCycle("2423003");
        AttendanceCycle latestCycle = attendanceCycleQueryService.getLatestCycle("2423003");

        // then
        assertThat(latestOpenCycle).isNull();
        assertThat(latestCycle).isNull();
    }

    @DisplayName("하교만 등록")
    @Test
    void success7() {
        // given
        cycleApplicationService.closeAttendanceCycle("2423002", date1);

        // when
        AttendanceCycle latestCycle = attendanceCycleQueryService.getLatestCycle("2423002");

        // then
        assertThat(latestCycle.getAttendTime()).isEqualTo(date1);
    }

    @DisplayName("외출만 등록")
    @Test
    void success8() {
        // given
        cycleApplicationService.createOutingCycle("2423002", date1, LogAction.식사);

        // when
        AttendanceCycle latestOpenCycle = attendanceCycleQueryService.getLatestOpenCycle("2423002");
        OutingCycle latestOpenOutingCycle = outingCycleQueryService.getLatestOpenOutingCycle("2423002");

        // then
        assertThat(latestOpenCycle.getAttendTime()).as("등교 시간").isEqualTo(date1);
        assertThat(latestOpenCycle.getLeaveTime()).as("하교 시간").isNull();
        assertThat(latestOpenOutingCycle.getOutingStartTime()).as("외출 시작 시간").isEqualTo(date1);
        assertThat(latestOpenOutingCycle.getOutingEndTime()).as("외출 종료 시간").isNull();
    }

    @DisplayName("복귀만 등록")
    @Test
    void success9() {
        // given
        cycleApplicationService.closeOutingCycle("2423002", date1);

        // when
        AttendanceCycle latestCycle = attendanceCycleQueryService.getLatestCycle("2423002");
        OutingCycle latestOutingCycle = outingCycleQueryService.getLatestOutingCycle("2423002");

        // then
        assertThat(latestCycle.getAttendTime()).as("등교 시간").isEqualTo(date1);
        assertThat(latestCycle.getLeaveTime()).as("하교 시간").isNull();
        assertThat(latestOutingCycle.getOutingStartTime()).as("외출 시작 시간").isEqualTo(date1);
        assertThat(latestOutingCycle.getOutingEndTime()).as("외출 종료 시간").isEqualTo(date1);
    }

    @DisplayName("재하교")
    @Test
    void success10() {
        // given
        cycleApplicationService.createAttendanceCycle("2423002", date1);
        cycleApplicationService.closeAttendanceCycle("2423002", date2);

        // when
        cycleApplicationService.closeAttendanceCycle("2423002", date3);

        // then
        AttendanceCycle latestCycle = attendanceCycleQueryService.getLatestCycle("2423002");
        assertThat(latestCycle.getAttendTime()).as("등교 시간").isEqualTo(date3);
        assertThat(latestCycle.getLeaveTime()).as("하교 시간").isEqualTo(date3);

    }

    @DisplayName("재외출")
    @Test
    void success11() {
        // given
        outingCycleCommandService.createOutingCycle("2423002", date1, LogAction.식사);
        OutingCycle firstOutingCycle = outingCycleQueryService.getLatestOutingCycle("2423002");

        // when
        outingCycleCommandService.createOutingCycle("2423002", date3, LogAction.식사);

        // then
        OutingCycle latestOpenOutingCycle = outingCycleQueryService.getLatestOpenOutingCycle("2423002");
        assertThat(firstOutingCycle.getOutingStartTime()).as("첫 외출 시작 시간").isEqualTo(date1);
        assertThat(firstOutingCycle.getOutingEndTime()).as("첫 외출 종료 시간").isEqualTo(date1);
        assertThat(latestOpenOutingCycle.getOutingStartTime()).as("외출 시작 시간").isEqualTo(date3);
        assertThat(latestOpenOutingCycle.getOutingEndTime()).isNull();
    }

    @DisplayName("등교, 하교 시간 동일")
    @Test
    void success12() {
        // given

        // when
        cycleApplicationService.createAttendanceCycle("2423002", date1);
        AttendanceCycle attendanceCycle = cycleApplicationService.closeAttendanceCycle("2423002", date1);

        // then
        assertThat(attendanceCycle.getAttendTime()).as("등교 시간").isEqualTo(date1);
        assertThat(attendanceCycle.getLeaveTime()).as("하교 시간").isEqualTo(date1);

    }

    @DisplayName("등교, 외출, 외출 복귀, 하교 시간 동일")
    @Test
    void success13() {
        // given

        // when
        cycleApplicationService.createAttendanceCycle("2423002", date1);
        cycleApplicationService.createOutingCycle("2423002", date1, LogAction.식사);
        cycleApplicationService.closeOutingCycle("2423002", date1);
        AttendanceCycle attendanceCycle = cycleApplicationService.closeAttendanceCycle("2423002", date1);

        // then
        assertThat(attendanceCycle.getAttendTime()).as("등교 시간").isEqualTo(date1);
        assertThat(attendanceCycle.getLeaveTime()).as("하교 시간").isEqualTo(date1);
        assertThat(attendanceCycle.getOutingCycles().get(0).getOutingStartTime()).as("외출 시간").isEqualTo(date1);
        assertThat(attendanceCycle.getOutingCycles().get(0).getOutingEndTime()).as("복귀 시간").isEqualTo(date1);
    }

    @DisplayName("복귀 없이 재외출")
    @Test
    void success14() {
        // given

        // when
        cycleApplicationService.createOutingCycle("2423002", date1, LogAction.식사);
        OutingCycle outingCycle = outingCycleQueryService.getLatestOpenOutingCycle("2423002");
        OutingCycle outingCycle2 = outingCycleCommandService.createOutingCycle("2423002", date3, LogAction.식사);

        // then
        assertThat(outingCycle.getOutingStartTime()).as("첫 외출 시작 시간").isEqualTo(date1);
        assertThat(outingCycle.getOutingEndTime()).as("첫 외출 종료 시간").isEqualTo(date1);
        assertThat(outingCycle2.getOutingStartTime()).as("두번째 외출 시작 시간").isEqualTo(date3);
    }

    @DisplayName("두 명 교차 생성")
    @Test
    void success15() {
        // given

        // when
        cycleApplicationService.createAttendanceCycle("2423002", date1);
        cycleApplicationService.createAttendanceCycle("2423007", date2);
        AttendanceCycle attendanceCycle1 = cycleApplicationService.closeAttendanceCycle("2423002", date3);
        AttendanceCycle attendanceCycle2 = cycleApplicationService.closeAttendanceCycle("2423007", date4);

        // then
        assertThat(attendanceCycle1.getAttendTime()).as("첫 사이클 등교 시간").isEqualTo(date1);
        assertThat(attendanceCycle1.getLeaveTime()).as("첫 사이클 하교 시간").isEqualTo(date3);
        assertThat(attendanceCycle2.getAttendTime()).as("두번째 사이클 등교 시간").isEqualTo(date2);
        assertThat(attendanceCycle2.getLeaveTime()).as("두번째 사이클 하교 시간").isEqualTo(date4);
    }

    @DisplayName("등교 시간 보다 빠른 시간에 하교")
    @Test
    void error1() {
        // given
        cycleApplicationService.createAttendanceCycle("2423002", date1);

        // when
        assertThatCode(() -> cycleApplicationService.closeAttendanceCycle("2423002", date1.minusMinutes(1)))
                .isInstanceOf(CycleException.class)
                .hasMessage("등교 보다 이른 하교 시간입니다.");

    }

    @DisplayName("등교 보다 빠른 시간에 외출")
    @Test
    void error2() {
        // given
        cycleApplicationService.createAttendanceCycle("2423002", date1);

        // when
        assertThatCode(() -> cycleApplicationService.createOutingCycle("2423002", date1.minusMinutes(1), LogAction.식사))
                .isInstanceOf(CycleException.class)
                .hasMessage("등교 시간보다 이른 외출입니다.");
    }

    @DisplayName("등교 보다 빠른 시간에 복귀")
    @Test
    void error3() {
        // given
        cycleApplicationService.createAttendanceCycle("2423002", date2);

        // when
        assertThatCode(() -> cycleApplicationService.closeOutingCycle("2423002", date1.minusMinutes(1)))
                .isInstanceOf(CycleException.class)
                .hasMessage("등교 시간보다 이른 외출입니다.");

    }

    @DisplayName("외출 보다 빠른 시간에 복귀")
    @Test
    void error4() {
        // given
        outingCycleCommandService.createOutingCycle("2423002", date1, LogAction.식사);

        // when
        assertThatCode(() -> cycleApplicationService.closeOutingCycle("2423002", date1.minusMinutes(1)))
                .isInstanceOf(CycleException.class)
                .hasMessage("외출 종료 시간이 외출 시작 시간보다 빠를 수 없습니다.");

    }
}
