package com.example.fingerprint_backend;

import com.example.fingerprint_backend.entity.*;
import com.example.fingerprint_backend.service.CleanHelperService;
import com.example.fingerprint_backend.service.CleanManagementService;
import com.example.fingerprint_backend.service.CleanScheduleGroupService;
import com.example.fingerprint_backend.types.CleanRole;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest
@ContextConfiguration(initializers = DotenvTestInitializer.class)
@Transactional
public class CleanScheduleGroupServiceTest {

    @Autowired
    private CleanScheduleGroupService cleanScheduleGroupService;
    @Autowired
    private CleanManagementService cleanManagementService;

    private static SchoolClass schoolClass;
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
        schoolClass = cleanManagementService.createSchoolClass("2027_A");
        member0 = cleanManagementService.createMember("2423007", "민정", "김", "2027_A", CleanRole.MANAGER);
        area1 = cleanManagementService.createArea("창조관 405호", "2027_A");
        area2 = cleanManagementService.createArea("창조관 304호", "2027_A");
        cleanManagementService.setMemberCleanArea("2423007", "창조관 405호");
        cleanManagementService.setDefaultArea("창조관 405호", "2027_A");
        member1 = cleanManagementService.createMember("2423001", "혁일", "권", "2027_A");
        member2 = cleanManagementService.createMember("2423002", "규민", "김", "2027_A");
        member3 = cleanManagementService.createMember("2423003", "근형", "김", "2027_A");
        member4 = cleanManagementService.createMember("2423005", "민규", "김", "2027_A");
        member5 = cleanManagementService.createMember("2423006", "민석", "김", "2027_A");
        member6 = cleanManagementService.createMember("2423008", "성관", "김", "2027_A");
        member7 = cleanManagementService.createMember("2423009", "성식", "김", "2027_A");
        member8 = cleanManagementService.createMember("2423011", "효찬", "김", "2027_A");
        date = LocalDate.now();
    }

    @DisplayName("청소 스케줄 생성")
    @Test
    void createCleanScheduleSchedule() {
        CleanSchedule cleanSchedule = cleanScheduleGroupService.createCleanSchedule(date, "창조관 405호", "2027_A");

        assertThat(cleanSchedule.getDate()).as("날짜 확인").isEqualTo(date);
        assertThat(cleanSchedule.getCleanArea()).as("청소 구역 확인").isEqualTo(area1);
        assertThat(cleanSchedule.getSchoolClass()).as("반 확인").isEqualTo(schoolClass);
        assertThat(cleanScheduleGroupService.getScheduleBySchoolClassName("2027_A", date.minusDays(1)).size()).as("스케줄 수").isEqualTo(1);
        assertThat(cleanScheduleGroupService.getScheduleBySchoolClassNameAndAreaName("창조관 405호", "2027_A", date.minusDays(1)).size()).as("구역 스케줄 수").isEqualTo(1);
    }

    @DisplayName("청소 스케줄 가져오기")
    @Test
    void getSchedule() {
        cleanScheduleGroupService.createCleanSchedule(date, "창조관 405호", "2027_A");
        CleanSchedule cleanSchedule = cleanScheduleGroupService.getCleanSchedule(date, "창조관 405호", "2027_A");
        assertThat(cleanSchedule.getDate()).as("날짜 확인").isEqualTo(date);
        assertThat(cleanSchedule.getCleanArea()).as("청소 구역 확인").isEqualTo(area1);
        assertThat(cleanSchedule.getSchoolClass()).as("반 확인").isEqualTo(schoolClass);
    }

    @DisplayName("존재하지 않는 청소 스케줄 요청.")
    @Test
    void getScheduleError() {
        assertThatCode(() -> cleanScheduleGroupService.getCleanSchedule(date.plusDays(1), "창조관 405호", "2027_A"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 청소 스케줄입니다.");
    }

    @DisplayName("청소 스케줄 취소")
    @Test
    void cancelSchedule() {
        cleanScheduleGroupService.createCleanSchedule(date, "창조관 405호", "2027_A");
        cleanScheduleGroupService.cancelCleanSchedule(date, "창조관 405호", "2027_A");
        assertThatCode(() -> cleanHelperService.validateCleanScheduleIsCanceled(date, "창조관 405호", "2027_A"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("취소된 청소 스케줄입니다.");
    }

    @DisplayName("취소된 청소 스케줄 복구")
    @Test
    void restoreSchedule() {
        cleanScheduleGroupService.createCleanSchedule(date, "창조관 405호", "2027_A");
        cleanScheduleGroupService.cancelCleanSchedule(date, "창조관 405호", "2027_A");
        cleanScheduleGroupService.restoreCleanSchedule(date, "창조관 405호", "2027_A");
        assertThatCode(() -> cleanHelperService.validateCleanScheduleIsCanceled(date, "창조관 405호", "2027_A"))
                .doesNotThrowAnyException();
    }

    @DisplayName("청소 스케줄 취소 후 생성으로 복구")
    @Test
    void cancelAndCreateCleanScheduleSchedule() {
        cleanScheduleGroupService.createCleanSchedule(date, "창조관 405호", "2027_A");
        cleanScheduleGroupService.cancelCleanSchedule(date, "창조관 405호", "2027_A");
        assertThatCode(() -> cleanScheduleGroupService.createCleanSchedule(date, "창조관 405호", "2027_A"))
                .as("취소된 스케줄 생성 시도")
                .doesNotThrowAnyException();
        CleanSchedule cleanSchedule = cleanScheduleGroupService.getCleanSchedule(date, "창조관 405호", "2027_A");
        assertThat(cleanSchedule.isCanceled()).as("취소된 스케줄").isFalse();
    }

    @DisplayName("청소 스케줄 삭제 후 재생성")
    @Test
    void deleteAndCreateCleanScheduleSchedule() {
        cleanScheduleGroupService.createCleanSchedule(date, "창조관 405호", "2027_A");
        cleanScheduleGroupService.deleteCleanSchedule(date, "창조관 405호", "2027_A");
        assertThat(cleanScheduleGroupService.getScheduleBySchoolClassName("2027_A", date.minusDays(1)).size()).as("스케줄 수").isEqualTo(0);
        cleanScheduleGroupService.createCleanSchedule(date, "창조관 405호", "2027_A");
        assertThat(cleanScheduleGroupService.getScheduleBySchoolClassName("2027_A", date.minusDays(1)).size()).as("스케줄 수").isEqualTo(1);
    }

    @DisplayName("취소되지 않은 마지막 스케줄을 가져옴")
    @Test
    void getLastSchedule() {
        // given
        cleanScheduleGroupService.createCleanSchedule(date, "창조관 405호", "2027_A");
        cleanScheduleGroupService.createCleanSchedule(date.plusDays(1), "창조관 405호", "2027_A");
        CleanSchedule target = cleanScheduleGroupService.createCleanSchedule(date.plusWeeks(1), "창조관 405호", "2027_A");
        cleanScheduleGroupService.createCleanSchedule(date.plusWeeks(1).plusDays(1), "창조관 405호", "2027_A");
        cleanScheduleGroupService.createCleanSchedule(date.plusWeeks(2), "창조관 405호", "2027_A");
        cleanScheduleGroupService.cancelCleanSchedule(date.plusWeeks(2), "창조관 405호", "2027_A");
        cleanScheduleGroupService.cancelCleanSchedule(date.plusWeeks(1).plusDays(1), "창조관 405호", "2027_A");

        // when
        CleanSchedule cleanSchedule = cleanScheduleGroupService.getLastCleanSchedule("창조관 405호", "2027_A");

        // then
        assertThat(cleanSchedule).isEqualTo(target);
    }

    @DisplayName("청소 그룹을 생성한다.")
    @Test
    void createCleanSchedule() {
        CleanGroup group = cleanScheduleGroupService.createGroup("창조관 405호", "2027_A", 4);

        assertThat(group.getCleanArea()).as("청소 구역 확인").isEqualTo(area1);
        assertThat(group.getSchoolClass()).as("반 확인").isEqualTo(schoolClass);
        assertThat(group.getMembers().size()).as("그룹 멤버 수").isEqualTo(0);
    }

    @DisplayName("청소 그룹에 멤버 추가, 삭제, 초과, 중복 시도")
    @Test
    void appendAndDeleteMemberToGroup() {
        CleanGroup group = cleanScheduleGroupService.createGroup("창조관 405호", "2027_A", 4);
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
        cleanManagementService.createMember("2423012", "번", "12", "2027_A");
        cleanManagementService.createMember("2423013", "번", "13", "2027_A");
        cleanManagementService.createMember("2423014", "번", "14", "2027_A");
        cleanManagementService.createMember("2423015", "번", "15", "2027_A");
        cleanManagementService.createMember("2423016", "번", "16", "2027_A");
        cleanManagementService.createMember("2423017", "번", "17", "2027_A");
        cleanManagementService.setMemberCleanArea("2423002", "창조관 304호");
        List<CleanMember> members = cleanManagementService.getMembersBySchoolClassNameAndAreaName("창조관 405호", "2027_A");
        cleanScheduleGroupService.createGroupsByRandom("창조관 405호", "2027_A", members, 4);
        List<CleanGroup> groups = cleanScheduleGroupService.getGroupsByAreaNameAndClassName("창조관 405호", "2027_A");

        groups.forEach(group -> {
            System.out.println(group.getId());
            group.getMembers().forEach(member -> {
                System.out.println(member.getFirstName());
            });
        });

        assertThat(groups.size()).as("그룹 수").isEqualTo(4);
    }

    @DisplayName("랜덤으로 그룹 생성 과정에서 발생하는 에러.")
    @Test
    void randomCreateGroupError() {
        List<CleanMember> members1 = cleanManagementService.getMembersBySchoolClassNameAndAreaName("창조관 304호", "2027_A");
        assertThatCode(() -> cleanScheduleGroupService.createGroupsByRandom("창조관 304호", "2027_A", members1, 4))
                .as("빈 리스트로 그룹 생성 시도")
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("리스트가 비어있습니다.");

        List<CleanMember> members2 = cleanManagementService.getMembersBySchoolClassNameAndAreaName("창조관 405호", "2027_A");
        assertThatCode(() -> cleanScheduleGroupService.createGroupsByRandom("창조관 405호", "2027_A", members2, 0))
                .as("그룹 최대 인원이 0인 경우")
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("그룹의 최대 인원은 1보다 작을 수 없습니다.");
    }
}
