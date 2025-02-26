package com.example.fingerprint_backend;

import com.example.fingerprint_backend.dto.clean.InfoResponse;
import com.example.fingerprint_backend.entity.CleanArea;
import com.example.fingerprint_backend.entity.CleanGroup;
import com.example.fingerprint_backend.entity.CleanMember;
import com.example.fingerprint_backend.entity.SchoolClass;
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

    @DisplayName("학생 추가")
    @Test
    void appendMember() {
        schoolClass = cleanManagementService.createSchoolClass("2027_A");
        cleanManagementService.createMember("2423007", "민정", "김", "2027_A");
        cleanManagementService.createMember("2423001", "혁일", "권", "2027_A");
        cleanManagementService.createMember("2423002", "규민", "김", "2027_A");
        cleanManagementService.createMember("2423003", "근형", "김", "2027_A");
        cleanManagementService.createMember("2423005", "민규", "김", "2027_A");
        cleanManagementService.createMember("2423006", "민석", "김", "2027_A");
        cleanManagementService.createMember("2423008", "성관", "김", "2027_A");
        cleanManagementService.createMember("2423009", "성식", "김", "2027_A");

        assertThat(schoolClass.getClassCleanMembers().size()).isEqualTo(8);
    }

    @DisplayName("기본 청소 구역 변경")
    @Test
    void appendArea() {
        schoolClass = cleanManagementService.createSchoolClass("2027_A");
        cleanManagementService.createMember("2423007", "민정", "김", "2027_A");
        CleanArea area1 = cleanManagementService.createArea("창조관 405호", "2027_A");
        cleanManagementService.createMember("2423001", "혁일", "권", "2027_A");
        cleanManagementService.createMember("2423002", "규민", "김", "2027_A");
        cleanManagementService.createMember("2423003", "근형", "김", "2027_A");
        CleanArea area2 = cleanManagementService.createArea("창조관 406호", "2027_A");
        cleanManagementService.setDefaultArea("창조관 406호", "2027_A");
        cleanManagementService.createMember("2423005", "민규", "김", "2027_A");
        cleanManagementService.createMember("2423006", "민석", "김", "2027_A");
        cleanManagementService.createMember("2423008", "성관", "김", "2027_A");
        cleanManagementService.createMember("2423009", "성식", "김", "2027_A");

        assertThat(schoolClass.getAreas().size()).as("학급의 구역 수").isEqualTo(2);
        assertThat(schoolClass.getDefaultArea().getName()).as("학급의 기본 구역 이름").isEqualTo("창조관 406호");
        assertThat(area1.getMembers().size()).as("구역 1의 멤버 수").isEqualTo(3);
        assertThat(area2.getMembers().size()).as("구역 2의 멤버 수").isEqualTo(4);
    }

    @DisplayName("멤버의 청소 구역 변경")
    @Test
    void changeArea() {
        schoolClass = cleanManagementService.createSchoolClass("2027_A");
        cleanManagementService.createMember("2423007", "민정", "김", "2027_A");
        CleanArea area1 = cleanManagementService.createArea("창조관 405호", "2027_A");
        cleanManagementService.createMember("2423001", "혁일", "권", "2027_A");
        CleanArea area2 = cleanManagementService.createArea("창조관 406호", "2027_A");
        cleanManagementService.setDefaultArea("창조관 406호", "2027_A");
        cleanManagementService.createMember("2423002", "규민", "김", "2027_A");
        cleanManagementService.createMember("2423003", "근형", "김", "2027_A");

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
        cleanManagementService.createSchoolClass("2027_A");
        cleanManagementService.createMember("2423007", "민정", "김", "2027_A");
        cleanManagementService.createArea("창조관 405호", "2027_A");
        cleanManagementService.setMemberCleanArea("2423007", "창조관 405호");
        cleanManagementService.createMember("2423001", "혁일", "권", "2027_A");
        cleanManagementService.createMember("2423002", "규민", "김", "2027_A");
        cleanManagementService.createMember("2423003", "근형", "김", "2027_A");
        cleanManagementService.createMember("2423005", "민규", "김", "2027_A");
        cleanManagementService.createMember("2423006", "민석", "김", "2027_A");
        cleanManagementService.createMember("2423008", "성관", "김", "2027_A");
        cleanManagementService.createMember("2423009", "성식", "김", "2027_A");

        List<CleanMember> members = cleanManagementService.getMembersBySchoolClassNameAndAreaName("창조관 405호", "2027_A");
        assertThat(members.size()).as("구역의 멤버 수").isEqualTo(8);

        cleanScheduleGroupService.createGroupsByRandom("창조관 405호", "2027_A", members, 3);
        CleanGroup lastGroup = cleanScheduleGroupService.getLastGroup("창조관 405호", "2027_A");
        assertThat(lastGroup.getMembers().size()).as("마지막 그룹의 멤버 수").isEqualTo(2);

        members = cleanManagementService.getMembersBySchoolClassNameAndAreaName("창조관 405호", "2027_A");
        cleanScheduleGroupService.createGroupsByRandom("창조관 405호", "2027_A", members, 3);
        CleanGroup lastGroup2 = cleanScheduleGroupService.getLastGroup("창조관 405호", "2027_A");
        assertThat(lastGroup2.getMembers().size()).as("마지막 그룹의 멤버 수").isEqualTo(1);
    }

    @Test
    void createGroupAndScheduleAndGetInfo() {
        cleanManagementService.createSchoolClass("2027_A");
        cleanManagementService.createMember("2423007", "민정", "김", "2027_A");
        cleanManagementService.createArea("창조관 405호", "2027_A");
        cleanManagementService.setMemberCleanArea("2423007", "창조관 405호");
        cleanManagementService.createMember("2423001", "혁일", "권", "2027_A");
        cleanManagementService.createMember("2423002", "규민", "김", "2027_A");
        cleanManagementService.createMember("2423003", "근형", "김", "2027_A");
        cleanManagementService.createMember("2423005", "민규", "김", "2027_A");
        cleanManagementService.createMember("2423006", "민석", "김", "2027_A");
        cleanManagementService.createMember("2423008", "성관", "김", "2027_A");
        cleanManagementService.createMember("2423009", "성식", "김", "2027_A");

        List<CleanMember> members = cleanManagementService.getMembersBySchoolClassNameAndAreaName("창조관 405호", "2027_A");
        assertThat(members.size()).as("구역의 멤버 수").isEqualTo(8);

        cleanScheduleGroupService.createGroupsByRandom("창조관 405호", "2027_A", members, 3);
        CleanGroup lastGroup = cleanScheduleGroupService.getLastGroup("창조관 405호", "2027_A");
        assertThat(lastGroup.getMembers().size()).as("마지막 그룹의 멤버 수").isEqualTo(2);
        cleanScheduleGroupService.createGroupsByRandom("창조관 405호", "2027_A", members, 3);
        assertThat(cleanScheduleGroupService.getGroupsByAreaNameAndClassName("창조관 405호", "2027_A").size()).as("그룹 수").isEqualTo(6);
        cleanScheduleGroupService.createCleanSchedules(LocalDate.now(), "창조관 405호", "2027_A", 1, Set.of(DayOfWeek.MONDAY), 6);
        List<InfoResponse> infoResponses = cleanOperationService.parsingInfos(
                cleanScheduleGroupService.getGroupsByAreaNameAndClassName("창조관 405호", "2027_A"),
                cleanScheduleGroupService.getScheduleByAreaNameAndSchoolClassName("창조관 405호", "2027_A", LocalDate.now()));
        infoResponses.forEach(infoResponse -> {
            System.out.println(infoResponse.getGroupId());
            infoResponse.getMembers().forEach(member -> {
                System.out.println(member.getGivenName());
            });
        });
        assertThat(infoResponses.size()).as("스케줄 수").isEqualTo(6);
    }
}
