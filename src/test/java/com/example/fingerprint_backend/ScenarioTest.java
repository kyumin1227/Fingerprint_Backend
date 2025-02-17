package com.example.fingerprint_backend;

import com.example.fingerprint_backend.entity.CleanArea;
import com.example.fingerprint_backend.entity.CleanMember;
import com.example.fingerprint_backend.entity.SchoolClass;
import com.example.fingerprint_backend.service.CleanManagementService;
import com.example.fingerprint_backend.types.CleanRole;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ContextConfiguration(initializers = DotenvTestInitializer.class)
@Transactional
public class ScenarioTest {

    @Autowired
    private CleanManagementService cleanManagementService;

    private SchoolClass schoolClass;

    @DisplayName("관리자의 초기 반 생성 및 학생 지정")
    @Test
    void createClassAndSetManager() {
        schoolClass = cleanManagementService.createSchoolClass("2027_A");
        CleanMember member = cleanManagementService.createMember("2423007", "김민정", "2027_A", CleanRole.MANAGER);

        assertThat(schoolClass.getManager()).isEqualTo(member);
    }

    @DisplayName("학생 추가")
    @Test
    void appendMember() {
        schoolClass = cleanManagementService.createSchoolClass("2027_A");
        cleanManagementService.createMember("2423007", "김민정", "2027_A", CleanRole.MANAGER);
        cleanManagementService.createMember("2423001", "권혁일", "2027_A");
        cleanManagementService.createMember("2423002", "김규민", "2027_A");
        cleanManagementService.createMember("2423003", "김근형", "2027_A");
        cleanManagementService.createMember("2423005", "김민규", "2027_A");
        cleanManagementService.createMember("2423006", "김민석", "2027_A");
        cleanManagementService.createMember("2423008", "김성관", "2027_A");
        cleanManagementService.createMember("2423009", "김성식", "2027_A");

        assertThat(schoolClass.getClassMembers().size()).isEqualTo(8);
    }

    @DisplayName("기본 청소 구역 변경")
    @Test
    void appendArea() {
        schoolClass = cleanManagementService.createSchoolClass("2027_A");
        cleanManagementService.createMember("2423007", "김민정", "2027_A", CleanRole.MANAGER);
        CleanArea area1 = cleanManagementService.createArea("창조관 405호", "2027_A");
        cleanManagementService.createMember("2423001", "권혁일", "2027_A");
        cleanManagementService.createMember("2423002", "김규민", "2027_A");
        cleanManagementService.createMember("2423003", "김근형", "2027_A");
        CleanArea area2 = cleanManagementService.createArea("창조관 406호", "2027_A");
        cleanManagementService.setDefaultArea("창조관 406호", "2027_A");
        cleanManagementService.createMember("2423005", "김민규", "2027_A");
        cleanManagementService.createMember("2423006", "김민석", "2027_A");
        cleanManagementService.createMember("2423008", "김성관", "2027_A");
        cleanManagementService.createMember("2423009", "김성식", "2027_A");

        assertThat(schoolClass.getAreas().size()).as("학급의 구역 수").isEqualTo(2);
        assertThat(schoolClass.getDefaultArea().getName()).as("학급의 기본 구역 이름").isEqualTo("창조관 406호");
        assertThat(area1.getMembers().size()).as("구역 1의 멤버 수").isEqualTo(3);
        assertThat(area2.getMembers().size()).as("구역 2의 멤버 수").isEqualTo(4);
    }

    @DisplayName("멤버의 청소 구역 변경")
    @Test
    void changeArea() {
        schoolClass = cleanManagementService.createSchoolClass("2027_A");
        cleanManagementService.createMember("2423007", "김민정", "2027_A", CleanRole.MANAGER);
        CleanArea area1 = cleanManagementService.createArea("창조관 405호", "2027_A");
        cleanManagementService.createMember("2423001", "권혁일", "2027_A");
        CleanArea area2 = cleanManagementService.createArea("창조관 406호", "2027_A");
        cleanManagementService.setDefaultArea("창조관 406호", "2027_A");
        cleanManagementService.createMember("2423002", "김규민", "2027_A");
        cleanManagementService.createMember("2423003", "김근형", "2027_A");

//        구역 변경
        cleanManagementService.setMemberCleanArea("2423001", "창조관 406호");
        cleanManagementService.setMemberCleanArea("2423007", "창조관 406호");

        assertThat(area1.getMembers().size()).as("구역 1의 멤버 수").isEqualTo(0);
        assertThat(area2.getMembers().size()).as("구역 2의 멤버 수").isEqualTo(4);

        cleanManagementService.setMemberCleanArea("2423001", "창조관 405호");

        assertThat(area1.getMembers().size()).as("구역 1의 멤버 수").isEqualTo(1);
        assertThat(area2.getMembers().size()).as("구역 2의 멤버 수").isEqualTo(3);
    }

    @DisplayName("청소 구역의 스케줄 생성 후 랜덤으로 그룹 할당")
    @Test
    void createSchedule() {
        cleanManagementService.createSchoolClass("2027_A");
        cleanManagementService.createMember("2423007", "김민정", "2027_A", CleanRole.MANAGER);
        cleanManagementService.createArea("창조관 405호", "2027_A");
        cleanManagementService.setMemberCleanArea("2423007", "창조관 405호");
        cleanManagementService.createMember("2423001", "권혁일", "2027_A");
        cleanManagementService.createMember("2423002", "김규민", "2027_A");
        cleanManagementService.createMember("2423003", "김근형", "2027_A");
        cleanManagementService.createMember("2423005", "김민규", "2027_A");
        cleanManagementService.createMember("2423006", "김민석", "2027_A");
        cleanManagementService.createMember("2423008", "김성관", "2027_A");
        cleanManagementService.createMember("2423009", "김성식", "2027_A");

        Set<CleanMember> members = cleanManagementService.getMembersBySchoolClassNameAndAreaName("2027_A", "창조관 405호");
        assertThat(members.size()).as("구역의 멤버 수").isEqualTo(8);

//        cleanScheduleService.setCleanScheduleByRandom("2027_A", "창조관 405호");
    }
}
