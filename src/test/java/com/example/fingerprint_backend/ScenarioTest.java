package com.example.fingerprint_backend;

import com.example.fingerprint_backend.dto.clean.InfoResponse;
import com.example.fingerprint_backend.entity.CleanArea;
import com.example.fingerprint_backend.entity.CleanGroup;
import com.example.fingerprint_backend.entity.CleanMember;
import com.example.fingerprint_backend.entity.SchoolClass;
import com.example.fingerprint_backend.scheduled.CleanScheduled;
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

@SpringBootTest
@ContextConfiguration(initializers = DotenvTestInitializer.class)
@Transactional
public class ScenarioTest {

    @Autowired
    private CleanManagementService cleanManagementService;

    private SchoolClass schoolClass;
    @Autowired
    private CleanScheduleGroupService cleanScheduleGroupService;
    @Autowired
    private CleanOperationService cleanOperationService;
    @Autowired
    private CleanHelperService cleanHelperService;
    @Autowired
    private CleanScheduled cleanScheduled;

    @DisplayName("학생 추가")
    @Test
    void appendMember() {
        schoolClass = cleanManagementService.createSchoolClass("2027_A");
        Long classId = schoolClass.getId();
        cleanManagementService.createMember("2423007", "민정", "김", classId, null);
        cleanManagementService.createMember("2423001", "혁일", "권", classId, null);
        cleanManagementService.createMember("2423002", "규민", "김", classId, null);
        cleanManagementService.createMember("2423003", "근형", "김", classId, null);
        cleanManagementService.createMember("2423005", "민규", "김", classId, null);
        cleanManagementService.createMember("2423006", "민석", "김", classId, null);
        cleanManagementService.createMember("2423008", "성관", "김", classId, null);
        cleanManagementService.createMember("2423009", "성식", "김", classId, null);

        assertThat(schoolClass.getClassCleanMembers().size()).isEqualTo(8);
    }

    @DisplayName("기본 청소 구역 변경")
    @Test
    void appendArea() {
        schoolClass = cleanManagementService.createSchoolClass("2027_A");
        Long classId = schoolClass.getId();
        cleanManagementService.createMember("2423007", "민정", "김", classId, null);
        CleanArea area1 = cleanManagementService.createArea("창조관 405호", classId);
        cleanManagementService.createMember("2423001", "혁일", "권", classId, null);
        cleanManagementService.createMember("2423002", "규민", "김", classId, null);
        cleanManagementService.createMember("2423003", "근형", "김", classId, null);
        CleanArea area2 = cleanManagementService.createArea("창조관 406호", classId);
        cleanManagementService.setDefaultArea("창조관 406호", classId);
        cleanManagementService.createMember("2423005", "민규", "김", classId, null);
        cleanManagementService.createMember("2423006", "민석", "김", classId, null);
        cleanManagementService.createMember("2423008", "성관", "김", classId, null);
        cleanManagementService.createMember("2423009", "성식", "김", classId, null);

        assertThat(schoolClass.getAreas().size()).as("학급의 구역 수").isEqualTo(2);
        assertThat(schoolClass.getDefaultArea().getName()).as("학급의 기본 구역 이름").isEqualTo("창조관 406호");
        assertThat(area1.getMembers().size()).as("구역 1의 멤버 수").isEqualTo(3);
        assertThat(area2.getMembers().size()).as("구역 2의 멤버 수").isEqualTo(4);
    }

    @DisplayName("멤버의 청소 구역 변경")
    @Test
    void changeArea() {
        schoolClass = cleanManagementService.createSchoolClass("2027_A");
        Long classId = schoolClass.getId();
        cleanManagementService.createMember("2423007", "민정", "김", classId, null);
        CleanArea area1 = cleanManagementService.createArea("창조관 405호", classId);
        cleanManagementService.createMember("2423001", "혁일", "권", classId, null);
        CleanArea area2 = cleanManagementService.createArea("창조관 406호", classId);
        cleanManagementService.setDefaultArea("창조관 406호", classId);
        cleanManagementService.createMember("2423002", "규민", "김", classId, null);
        cleanManagementService.createMember("2423003", "근형", "김", classId, null);

//        구역 변경
        cleanManagementService.setMemberCleanArea("2423001", "창조관 406호");
        cleanManagementService.setMemberCleanArea("2423007", "창조관 406호");

        assertThat(area1.getMembers().size()).as("구역 1의 멤버 수").isEqualTo(0);
        assertThat(area2.getMembers().size()).as("구역 2의 멤버 수").isEqualTo(4);

        cleanManagementService.setMemberCleanArea("2423001", "창조관 405호");

        assertThat(area1.getMembers().size()).as("구역 1의 멤버 수").isEqualTo(1);
        assertThat(area2.getMembers().size()).as("구역 2의 멤버 수").isEqualTo(3);
    }

    @DisplayName("랜덤으로 그룹 할당")
    @Test
    void createGroupByRandom() {
        Long classId = cleanManagementService.createSchoolClass("2027_A").getId();
        cleanManagementService.createMember("2423007", "민정", "김", classId, null);
        cleanManagementService.createArea("창조관 405호", classId);
        cleanManagementService.setMemberCleanArea("2423007", "창조관 405호");
        cleanManagementService.createMember("2423001", "혁일", "권", classId, null);
        cleanManagementService.createMember("2423002", "규민", "김", classId, null);
        cleanManagementService.createMember("2423003", "근형", "김", classId, null);
        cleanManagementService.createMember("2423005", "민규", "김", classId, null);
        cleanManagementService.createMember("2423006", "민석", "김", classId, null);
        cleanManagementService.createMember("2423008", "성관", "김", classId, null);
        cleanManagementService.createMember("2423009", "성식", "김", classId, null);

        List<CleanMember> members = cleanManagementService.getMembersByAreaNameAndClassId("창조관 405호", classId);
        assertThat(members.size()).as("구역의 멤버 수").isEqualTo(8);

        cleanScheduleGroupService.createGroupsByRandom("창조관 405호", classId, members, 3);
        CleanGroup lastGroup = cleanScheduleGroupService.getLastGroup("창조관 405호", classId);
        assertThat(lastGroup.getMembers().size()).as("마지막 그룹의 멤버 수").isEqualTo(2);

        members = cleanManagementService.getMembersByAreaNameAndClassId("창조관 405호", classId);
        cleanScheduleGroupService.createGroupsByRandom("창조관 405호", classId, members, 3);
        CleanGroup lastGroup2 = cleanScheduleGroupService.getLastGroup("창조관 405호", classId);
        assertThat(lastGroup2.getMembers().size()).as("마지막 그룹의 멤버 수").isEqualTo(1);
    }

    @DisplayName("그룹 생성 및 스케줄링 후 정보 가져오기")
    @Test
    void createGroupAndScheduleAndGetInfo() {
        Long classId = cleanManagementService.createSchoolClass("2027_A").getId();
        cleanManagementService.createMember("2423007", "민정", "김", classId, null);
        cleanManagementService.createArea("창조관 405호", classId);
        cleanManagementService.setMemberCleanArea("2423007", "창조관 405호");
        cleanManagementService.createMember("2423001", "혁일", "권", classId, null);
        cleanManagementService.createMember("2423002", "규민", "김", classId, null);
        cleanManagementService.createMember("2423003", "근형", "김", classId, null);
        cleanManagementService.createMember("2423005", "민규", "김", classId, null);
        cleanManagementService.createMember("2423006", "민석", "김", classId, null);
        cleanManagementService.createMember("2423008", "성관", "김", classId, null);
        cleanManagementService.createMember("2423009", "성식", "김", classId, null);

        List<CleanMember> members = cleanManagementService.getMembersByAreaNameAndClassId("창조관 405호", classId);
        assertThat(members.size()).as("구역의 멤버 수").isEqualTo(8);

        cleanScheduleGroupService.createGroupsByRandom("창조관 405호", classId, members, 3);
        CleanGroup lastGroup = cleanScheduleGroupService.getLastGroup("창조관 405호", classId);
        assertThat(lastGroup.getMembers().size()).as("마지막 그룹의 멤버 수").isEqualTo(2);
        cleanScheduleGroupService.createGroupsByRandom("창조관 405호", classId, members, 3);
        assertThat(cleanScheduleGroupService.getGroupsByAreaNameAndClassId("창조관 405호", classId).size()).as("그룹 수").isEqualTo(6);
        cleanScheduleGroupService.createCleanSchedules(LocalDate.now(), "창조관 405호", classId, 1, Set.of(DayOfWeek.MONDAY), 6);
        List<InfoResponse> infoResponses = cleanOperationService.parsingInfos(
                cleanScheduleGroupService.getGroupsByAreaNameAndClassId("창조관 405호", classId),
                cleanScheduleGroupService.getScheduleByAreaNameAndSchoolClassId("창조관 405호", classId, LocalDate.now()));
        infoResponses.forEach(infoResponse -> {
            System.out.println(infoResponse.getGroupId());
            infoResponse.getMembers().forEach(member -> {
                System.out.println(member.getGivenName());
            });
        });
        assertThat(infoResponses.size()).as("스케줄 수").isEqualTo(6);
    }

    @DisplayName("멤버 삭제")
    @Test
    void deleteMember() {
        Long classId = cleanManagementService.createSchoolClass("2027_A").getId();
        CleanArea area = cleanManagementService.createArea("창조관 405호", classId, Set.of(DayOfWeek.MONDAY), 1);
        cleanManagementService.createMember("2423007", "민정", "김", classId, area);
        cleanManagementService.createMember("2423001", "혁일", "권", classId, area);
        cleanManagementService.createMember("2423002", "규민", "김", classId, area);

        cleanScheduleGroupService.createCleanSchedules(LocalDate.now(), "창조관 405호", classId, 1, Set.of(DayOfWeek.MONDAY), 2);
        List<CleanMember> members = cleanManagementService.getMembersByAreaNameAndClassId("창조관 405호", classId);
        cleanScheduleGroupService.createGroupsByRandom("창조관 405호", classId, members, 3);
        cleanScheduleGroupService.createGroupsByRandom("창조관 405호", classId, members, 3);

        List<CleanMember> groupMembers = cleanScheduleGroupService.getFirstGroup("창조관 405호", classId).getMembers();
        assertThat(groupMembers.size()).as("그룹의 멤버 수").isEqualTo(3);

        cleanManagementService.deleteMember("2423002");
        assertThat(cleanHelperService.getSchoolClassById(classId).getClassCleanMembers().size()).as("반의 멤버 수").isEqualTo(2);
        assertThat(cleanScheduleGroupService.getFirstGroup("창조관 405호", classId).getMembers().size()).as("그룹의 멤버 수").isEqualTo(2);
        assertThat(cleanScheduleGroupService.getLastGroup("창조관 405호", classId).getMembers().size()).as("그룹의 멤버 수").isEqualTo(2);
        assertThat(cleanHelperService.getCleanAreaByNameAndClassId("창조관 405호", classId).getMembers().size()).as("구역의 멤버 수").isEqualTo(2);

    }

    @DisplayName("멤버 구역 변경")
    @Test
    void changeCleanArea() {
        Long classId = cleanManagementService.createSchoolClass("2027_A").getId();
        CleanArea area1 = cleanManagementService.createArea("창조관 405호", classId, Set.of(DayOfWeek.MONDAY), 1);
        CleanArea area2 = cleanManagementService.createArea("창조관 406호", classId, Set.of(DayOfWeek.MONDAY), 1);
        cleanManagementService.createMember("2423007", "민정", "김", classId, area1);
        cleanManagementService.createMember("2423001", "혁일", "권", classId, area1);
        cleanManagementService.createMember("2423002", "규민", "김", classId, area1);

        cleanScheduleGroupService.createCleanSchedules(LocalDate.now(), "창조관 405호", classId, 1, Set.of(DayOfWeek.MONDAY), 2);
        List<CleanMember> members = cleanManagementService.getMembersByAreaNameAndClassId("창조관 405호", classId);
        cleanScheduleGroupService.createGroupsByRandom("창조관 405호", classId, members, 3);
        cleanScheduleGroupService.createGroupsByRandom("창조관 405호", classId, members, 3);

        List<CleanMember> groupMembers = cleanScheduleGroupService.getFirstGroup("창조관 405호", classId).getMembers();
        assertThat(groupMembers.size()).as("그룹의 멤버 수").isEqualTo(3);

        cleanManagementService.setMemberCleanArea("2423002", "창조관 406호");
        assertThat(cleanHelperService.getCleanMemberByStudentNumber("2423002").getCleanArea().getName()).as("멤버의 구역 이름").isEqualTo("창조관 406호");
        assertThat(cleanScheduleGroupService.getFirstGroup("창조관 405호", classId).getMembers().size()).as("그룹의 멤버 수").isEqualTo(2);
        assertThat(cleanScheduleGroupService.getLastGroup("창조관 405호", classId).getMembers().size()).as("그룹의 멤버 수").isEqualTo(2);
        assertThat(cleanHelperService.getCleanAreaByNameAndClassId("창조관 405호", classId).getMembers().size()).as("구역의 멤버 수").isEqualTo(2);
        assertThat(cleanHelperService.getCleanAreaByNameAndClassId("창조관 406호", classId).getMembers().size()).as("구역의 멤버 수").isEqualTo(1);
        assertThat(cleanHelperService.getSchoolClassByName("2027_A").getClassCleanMembers().size()).as("반의 멤버 수").isEqualTo(3);
    }

    @DisplayName("멤버 수정")
    @Test
    void updateMember() {
        Long classId = cleanManagementService.createSchoolClass("2027_A").getId();
        CleanArea area1 = cleanManagementService.createArea("창조관_405호", classId);
        CleanArea area2 = cleanManagementService.createArea("창조관_406호", classId);
        cleanManagementService.createMember("2423002", "규민", "김", classId, area1);
        cleanScheduleGroupService.createGroupsByRandom("창조관_405호", classId, cleanManagementService.getMembersByAreaNameAndClassId("창조관_405호", classId), 3);

        cleanManagementService.updateMember("2423002", "변경", "성", "창조관_406호");
        assertThat(cleanHelperService.getCleanMemberByStudentNumber("2423002").getGivenName()).as("이름 변경").isEqualTo("변경");
        assertThat(cleanHelperService.getCleanMemberByStudentNumber("2423002").getFamilyName()).as("성 변경").isEqualTo("성");
        assertThat(cleanHelperService.getCleanMemberByStudentNumber("2423002").getCleanArea().getName()).as("구역 변경").isEqualTo("창조관_406호");
        assertThat(area2.getMembers().size()).as("구역의 멤버 수").isEqualTo(1);
        assertThat(cleanScheduleGroupService.getGroupsByAreaNameAndClassId("창조관_405호", classId).get(0).getMembers().size()).as("그룹의 멤버 수").isEqualTo(0);
    }

    @DisplayName("청소 스케줄 자동 추가 1 (월요일, 스케줄 10개)")
    @Test
    void autoAppendSchedule_1() {
        Long classId = cleanManagementService.createSchoolClass("2027_A").getId();
        CleanArea area1 = cleanManagementService.createArea("창조관 405호", classId, Set.of(DayOfWeek.MONDAY), 1);
        area1.setDisplay(10);
        cleanScheduled.createScheduleIfNeeded();

        cleanScheduleGroupService.getScheduleByAreaNameAndSchoolClassId("창조관 405호", classId, LocalDate.now()).forEach(schedule -> {
            System.out.println("schedule.getDate() = " + schedule.getDate());
        });

        assertThat(cleanScheduleGroupService.getScheduleByAreaNameAndSchoolClassId("창조관 405호", classId, LocalDate.now()).size()).as("스케줄 수 10개").isEqualTo(10);
    }

    @DisplayName("청소 스케줄 자동 추가 2 (월요일, 스케줄 10개)")
    @Test
    void autoAppendSchedule_2() {
        Long classId = cleanManagementService.createSchoolClass("2027_A").getId();
        CleanArea area1 = cleanManagementService.createArea("창조관 405호", classId, Set.of(DayOfWeek.MONDAY), 1);
        cleanScheduled.createScheduleIfNeeded();
        area1.setDisplay(10);
        area1.setDays(Set.of(DayOfWeek.SUNDAY, DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY));
        cleanScheduled.createScheduleIfNeeded();

        cleanScheduleGroupService.getScheduleByAreaNameAndSchoolClassId("창조관 405호", classId, LocalDate.now()).forEach(schedule -> {
            System.out.println("schedule.getDate() = " + schedule.getDate());
        });

        assertThat(cleanScheduleGroupService.getScheduleByAreaNameAndSchoolClassId("창조관 405호", classId, LocalDate.now()).size()).as("스케줄 수 10개").isEqualTo(10);
    }

    @DisplayName("청소 그룹 자동 추가")
    @Test
    void autoAppendGroup() {
        Long classId = cleanManagementService.createSchoolClass("2027_A").getId();
        CleanArea area1 = cleanManagementService.createArea("창조관 405호", classId, Set.of(DayOfWeek.MONDAY), 1);
        area1.setDisplay(3);
        cleanManagementService.createMember("2423007", "민정", "김", classId, area1);
        cleanManagementService.createMember("2423001", "혁일", "권", classId, area1);
        cleanManagementService.createMember("2423002", "규민", "김", classId, area1);
        cleanScheduled.createGroupIfNeeded();
        cleanManagementService.createMember("2423003", "근형", "김", classId, area1);
        area1.setDisplay(5);
        cleanScheduled.createGroupIfNeeded();

        cleanScheduleGroupService.getGroupsByAreaNameAndClassId("창조관 405호", classId).forEach(group -> {
            System.out.println("group.getMembers() = " + group.getMembers());
            group.getMembers().forEach(member -> {
                System.out.println("member.getGivenName() = " + member.getGivenName());
            });
        });

//        앞에 채웠기 때문에 3명
        assertThat(cleanScheduleGroupService.getLastGroup("창조관 405호", classId).getMembers().size()).as("마지막 그룹의 멤버 수").isEqualTo(3);

        assertThat(cleanScheduleGroupService.getGroupsByAreaNameAndClassIdAndIsCleaned("창조관 405호", classId, false).size()).as("그룹 수").isEqualTo(5);
    }
}
