package com.example.fingerprint_backend;

import com.example.fingerprint_backend.dto.clean.InfoResponse;
import com.example.fingerprint_backend.entity.*;
import com.example.fingerprint_backend.service.CleanHelperService;
import com.example.fingerprint_backend.service.CleanManagementService;
import com.example.fingerprint_backend.service.CleanOperationService;
import com.example.fingerprint_backend.service.CleanScheduleGroupService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest
//@ContextConfiguration(initializers = DotenvTestInitializer.class)
@Transactional
public class CleanScheduleGroupServiceTest {

    @Autowired
    private CleanScheduleGroupService cleanScheduleGroupService;
    @Autowired
    private CleanManagementService cleanManagementService;
    @Autowired
    private CleanOperationService cleanOperationService;

    private static SchoolClass schoolClass;
    private static Long classId;
    private static CleanMember member0;
    private static CleanMember member1;
    private static CleanMember member2;
    private static CleanMember member3;
    private static CleanMember member4;
    private static CleanMember member5;
    private static CleanMember member6;
    private static CleanMember member7;
    private static CleanMember member8;
    private static CleanArea area1;
    private static CleanArea area2;
    private static LocalDate date;
    @Autowired
    private CleanHelperService cleanHelperService;


    @DisplayName("테스트 값 생성")
    @BeforeEach
    void setUp() {
        schoolClass = cleanManagementService.createSchoolClass("2024");
        classId = schoolClass.getId();
        member0 = cleanManagementService.createMember("2423007", "민정", "김", classId, null);
        area1 = cleanManagementService.createArea("창조관 405호", classId);
        area2 = cleanManagementService.createArea("창조관 304호", classId);
        cleanManagementService.setMemberCleanArea("2423007", "창조관 405호");
        cleanManagementService.setDefaultArea("창조관 405호", classId);
        member1 = cleanManagementService.createMember("2423001", "혁일", "권", classId, null);
        member2 = cleanManagementService.createMember("2423002", "규민", "김", classId, null);
        member3 = cleanManagementService.createMember("2423003", "근형", "김", classId, null);
        member4 = cleanManagementService.createMember("2423005", "민규", "김", classId, null);
        member5 = cleanManagementService.createMember("2423006", "민석", "김", classId, null);
        member6 = cleanManagementService.createMember("2423008", "성관", "김", classId, null);
        member7 = cleanManagementService.createMember("2423009", "성식", "김", classId, null);
        member8 = cleanManagementService.createMember("2423011", "효찬", "김", classId, null);
        date = LocalDate.now();
    }

    @DisplayName("청소 스케줄 생성")
    @Test
    void createAndRestoreCleanScheduleSchedule() {
        Long classId = schoolClass.getId();
        CleanSchedule cleanSchedule = cleanScheduleGroupService.createAndRestoreCleanSchedule(date, "창조관 405호", classId);

        assertThat(cleanSchedule.getDate()).as("날짜 확인").isEqualTo(date);
        assertThat(cleanSchedule.getCleanArea()).as("청소 구역 확인").isEqualTo(area1);
        assertThat(cleanSchedule.getSchoolClass()).as("반 확인").isEqualTo(schoolClass);
        assertThat(cleanScheduleGroupService.getScheduleBySchoolClassId(classId, date.minusDays(1)).size()).as("스케줄 수").isEqualTo(1);
        assertThat(cleanScheduleGroupService.getScheduleByAreaNameAndSchoolClassId("창조관 405호", classId, date.minusDays(1)).size()).as("구역 스케줄 수").isEqualTo(1);
    }

    @DisplayName("청소 스케줄 가져오기")
    @Test
    void getSchedule() {
        cleanScheduleGroupService.createAndRestoreCleanSchedule(date, "창조관 405호", classId);
        CleanSchedule cleanSchedule = cleanScheduleGroupService.getCleanSchedule(date, "창조관 405호", classId);
        assertThat(cleanSchedule.getDate()).as("날짜 확인").isEqualTo(date);
        assertThat(cleanSchedule.getCleanArea()).as("청소 구역 확인").isEqualTo(area1);
        assertThat(cleanSchedule.getSchoolClass()).as("반 확인").isEqualTo(schoolClass);
    }

    @DisplayName("존재하지 않는 청소 스케줄 요청.")
    @Test
    void getScheduleError() {
        assertThatCode(() -> cleanScheduleGroupService.getCleanSchedule(date.plusDays(1), "창조관 405호", classId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 청소 스케줄입니다.");
    }

    @DisplayName("청소 스케줄 취소")
    @Test
    void cancelSchedule() {
        cleanScheduleGroupService.createAndRestoreCleanSchedule(date, "창조관 405호", classId);
        cleanScheduleGroupService.cancelCleanSchedule(date, "창조관 405호", classId);
        assertThatCode(() -> cleanHelperService.validateCleanScheduleIsCanceled(date, "창조관 405호", classId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("취소된 청소 스케줄입니다.");
    }

    @DisplayName("취소된 청소 스케줄 복구")
    @Test
    void restoreSchedule() {
        cleanScheduleGroupService.createAndRestoreCleanSchedule(date, "창조관 405호", classId);
        cleanScheduleGroupService.cancelCleanSchedule(date, "창조관 405호", classId);
        cleanScheduleGroupService.restoreCleanSchedule(date, "창조관 405호", classId);
        assertThatCode(() -> cleanHelperService.validateCleanScheduleIsCanceled(date, "창조관 405호", classId))
                .doesNotThrowAnyException();
    }

    @DisplayName("청소 스케줄 취소 후 생성으로 복구")
    @Test
    void cancelAndCreateAndRestoreCleanScheduleSchedule() {
        cleanScheduleGroupService.createAndRestoreCleanSchedule(date, "창조관 405호", classId);
        cleanScheduleGroupService.cancelCleanSchedule(date, "창조관 405호", classId);
        assertThatCode(() -> cleanScheduleGroupService.createAndRestoreCleanSchedule(date, "창조관 405호", classId))
                .as("취소된 스케줄 생성 시도")
                .doesNotThrowAnyException();
        CleanSchedule cleanSchedule = cleanScheduleGroupService.getCleanSchedule(date, "창조관 405호", classId);
        assertThat(cleanSchedule.isCanceled()).as("취소된 스케줄").isFalse();
    }

    @DisplayName("청소 스케줄 삭제 후 재생성")
    @Test
    void deleteAndCreateAndRestoreCleanScheduleSchedule() {
        cleanScheduleGroupService.createAndRestoreCleanSchedule(date, "창조관 405호", classId);
        cleanScheduleGroupService.deleteCleanSchedule(date, "창조관 405호", classId);
        assertThat(cleanScheduleGroupService.getScheduleBySchoolClassId(classId, date.minusDays(1)).size()).as("스케줄 수").isEqualTo(0);
        cleanScheduleGroupService.createAndRestoreCleanSchedule(date, "창조관 405호", classId);
        assertThat(cleanScheduleGroupService.getScheduleBySchoolClassId(classId, date.minusDays(1)).size()).as("스케줄 수").isEqualTo(1);
    }

    @DisplayName("취소되지 않은 마지막 스케줄을 가져옴")
    @Test
    void getLastSchedule() {
        // given
        cleanScheduleGroupService.createAndRestoreCleanSchedule(date, "창조관 405호", classId);
        cleanScheduleGroupService.createAndRestoreCleanSchedule(date.plusDays(1), "창조관 405호", classId);
        CleanSchedule target = cleanScheduleGroupService.createAndRestoreCleanSchedule(date.plusWeeks(1), "창조관 405호", classId);
        cleanScheduleGroupService.createAndRestoreCleanSchedule(date.plusWeeks(1).plusDays(1), "창조관 405호", classId);
        cleanScheduleGroupService.createAndRestoreCleanSchedule(date.plusWeeks(2), "창조관 405호", classId);
        cleanScheduleGroupService.cancelCleanSchedule(date.plusWeeks(2), "창조관 405호", classId);
        cleanScheduleGroupService.cancelCleanSchedule(date.plusWeeks(1).plusDays(1), "창조관 405호", classId);

        // when
        CleanSchedule cleanSchedule = cleanScheduleGroupService.getLastCleanSchedule("창조관 405호", classId);

        // then
        assertThat(cleanSchedule).isEqualTo(target);
    }

    @DisplayName("청소 그룹을 생성한다.")
    @Test
    void createAndRestoreCleanSchedule() {
        CleanGroup group = cleanScheduleGroupService.createGroup("창조관 405호", classId, 4);

        assertThat(group.getCleanArea()).as("청소 구역 확인").isEqualTo(area1);
        assertThat(group.getSchoolClass()).as("반 확인").isEqualTo(schoolClass);
        assertThat(group.getMembers().size()).as("그룹 멤버 수").isEqualTo(0);
    }

    @DisplayName("청소 그룹에 멤버 추가, 삭제, 초과, 중복 시도")
    @Test
    void appendAndDeleteMemberToGroup() {
        CleanGroup group = cleanScheduleGroupService.createGroup("창조관 405호", classId, 4);
        cleanScheduleGroupService.appendMemberToGroup(group.getId(), member1.getStudentNumber());
        cleanScheduleGroupService.appendMemberToGroup(group.getId(), member2.getStudentNumber());

//        중복 추가 시도
        assertThatCode(() -> cleanScheduleGroupService.appendMemberToGroup(group.getId(), member1.getStudentNumber()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 그룹에 존재하는 멤버입니다.");

        cleanScheduleGroupService.appendMemberToGroup(group.getId(), member3.getStudentNumber());
        cleanScheduleGroupService.appendMemberToGroup(group.getId(), member4.getStudentNumber());

        assertThat(group.getMembers().size()).as("그룹 멤버 수").isEqualTo(4);

//        최대 인원 초과 시도
        assertThatCode(() -> cleanScheduleGroupService.appendMemberToGroup(group.getId(), member5.getStudentNumber()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("더 이상 그룹에 추가할 수 없습니다.");

        cleanScheduleGroupService.removeMemberFromGroup(group.getId(), member1.getStudentNumber());
        cleanScheduleGroupService.removeMemberFromGroup(group.getId(), member2.getStudentNumber());

//        존재 하지 않는 멤버 삭제 시도
        assertThatCode(() -> cleanScheduleGroupService.removeMemberFromGroup(group.getId(), member1.getStudentNumber()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("그룹에 존재하지 않는 멤버입니다.");

        cleanScheduleGroupService.removeMemberFromGroup(group.getId(), member3.getStudentNumber());
        cleanScheduleGroupService.removeMemberFromGroup(group.getId(), member4.getStudentNumber());

        assertThat(group.getMembers().size()).as("그룹 멤버 수").isEqualTo(0);
    }

    @DisplayName("랜덤으로 그룹 생성.")
    @Test
    void randomCreateGroup() {
        cleanManagementService.createMember("2423012", "번", "12", classId, null);
        cleanManagementService.createMember("2423013", "번", "13", classId, null);
        cleanManagementService.createMember("2423014", "번", "14", classId, null);
        cleanManagementService.createMember("2423015", "번", "15", classId, null);
        cleanManagementService.createMember("2423016", "번", "16", classId, null);
        cleanManagementService.createMember("2423017", "번", "17", classId, null);
        cleanManagementService.setMemberCleanArea("2423002", "창조관 304호");
        List<CleanMember> members = cleanManagementService.getMembersByAreaNameAndClassId("창조관 405호", classId);
        cleanScheduleGroupService.createGroupsByRandom("창조관 405호", classId, members, 4);
        List<CleanGroup> groups = cleanScheduleGroupService.getGroupsByAreaNameAndClassId("창조관 405호", classId);

        groups.forEach(group -> {
            System.out.println(group.getId());
            group.getMembers().forEach(member -> {
                System.out.println(member.getGivenName());
            });
        });

        assertThat(groups.size()).as("그룹 수").isEqualTo(4);
    }

    @DisplayName("랜덤으로 그룹 생성 과정에서 발생하는 에러.")
    @Test
    void randomCreateGroupError() {
        List<CleanMember> members1 = cleanManagementService.getMembersByAreaNameAndClassId("창조관 304호", classId);
        assertThatCode(() -> cleanScheduleGroupService.createGroupsByRandom("창조관 304호", classId, members1, 4))
                .as("빈 리스트로 그룹 생성 시도")
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("리스트가 비어있습니다.");

        List<CleanMember> members2 = cleanManagementService.getMembersByAreaNameAndClassId("창조관 405호", classId);
        assertThatCode(() -> cleanScheduleGroupService.createGroupsByRandom("창조관 405호", classId, members2, 0))
                .as("그룹 최대 인원이 0인 경우")
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("그룹의 최대 인원은 1보다 작을 수 없습니다.");
    }

    @DisplayName("스케줄 생성")
    @Test
    void createSchedules1() {
        cleanScheduleGroupService.createCleanSchedules(date, "창조관 405호", classId, 1, Set.of(DayOfWeek.FRIDAY), 4);
        List<CleanSchedule> schedules = cleanScheduleGroupService.getScheduleByAreaNameAndSchoolClassId("창조관 405호", classId, date);
        schedules.forEach(schedule -> {
            System.out.println(schedule.getDate() + " " + schedule.getDate().getDayOfWeek());
        });
        assertThat(schedules.size()).as("스케줄 수").isEqualTo(4);
    }

    @DisplayName("스케줄 생성 후 취소 후 재생성")
    @Test
    void createSchedules2() {
        cleanScheduleGroupService.createCleanSchedules(date, "창조관 405호", classId, 2, Set.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY), 8);
        List<CleanSchedule> schedules = cleanScheduleGroupService.getScheduleByAreaNameAndSchoolClassId("창조관 405호", classId, date);
        schedules.forEach(schedule -> {
            System.out.println(schedule.getDate() + " " + schedule.getDate().getDayOfWeek());
            schedule.setCanceled(true);
        });
        schedules.get(0).setCanceled(false);
        schedules.get(1).setCanceled(false);
        assertThat(schedules.size()).as("스케줄 수").isEqualTo(8);
        cleanScheduleGroupService.createCleanSchedules(date, "창조관 405호", classId, 2, Set.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY), 8);
        schedules = cleanScheduleGroupService.getScheduleByAreaNameAndSchoolClassId("창조관 405호", classId, date);
        schedules.forEach(schedule -> {
            System.out.println(schedule.getDate() + " " + schedule.getDate().getDayOfWeek());
        });
        assertThat(schedules.size()).as("스케줄 수").isEqualTo(16);
    }

    @DisplayName("그룹과 스케줄을 생성하고 반환값 파싱")
    @Test
    void parseInfo() {
//        given
        List<CleanMember> members = cleanManagementService.getMembersByAreaNameAndClassId("창조관 405호", classId);
        cleanScheduleGroupService.createGroupsByRandom("창조관 405호", classId, members, 4);
        cleanScheduleGroupService.createGroupsByRandom("창조관 405호", classId, members, 4);
        cleanScheduleGroupService.createCleanSchedules(date, "창조관 405호", classId, 1, Set.of(DayOfWeek.FRIDAY), 4);

        List<CleanGroup> groups = cleanScheduleGroupService.getGroupsByAreaNameAndClassId("창조관 405호", classId);
        List<CleanSchedule> schedules = cleanScheduleGroupService.getScheduleByAreaNameAndSchoolClassId("창조관 405호", classId, date);

        List<InfoResponse> infoResponses = cleanOperationService.parsingInfos(groups, schedules);

        assertThat(infoResponses.size()).as("정보 수").isEqualTo(4);
    }
}
