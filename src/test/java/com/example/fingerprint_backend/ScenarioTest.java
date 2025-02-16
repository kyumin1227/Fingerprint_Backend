package com.example.fingerprint_backend;

import com.example.fingerprint_backend.entity.CleanMember;
import com.example.fingerprint_backend.entity.SchoolClass;
import com.example.fingerprint_backend.service.CleanManagementService;
import com.example.fingerprint_backend.types.CleanRole;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ContextConfiguration(initializers = DotenvTestInitializer.class)
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ScenarioTest {

    @Autowired
    private CleanManagementService cleanManagementService;

    private SchoolClass schoolClass;

    @DisplayName("관리자의 초기 반 생성 및 학생 지정")
    @Test
    @Order(1)
    void createClassAndSetManager() {
        schoolClass = cleanManagementService.createSchoolClass("2027_A");
        CleanMember member = cleanManagementService.createMember("2423007", "김민정", "2027_A", CleanRole.MANAGER);

//        assertThat(schoolClass.getManager()).isEqualTo(member);
    }

    @DisplayName("학생 추가")
    @Test
    @Order(2)
    void appendMember() {
        schoolClass = cleanManagementService.createSchoolClass("2027_A");
//        CleanMember member = cleanManagementService.createMember("2423007", "김민정", "2027_A", CleanRole.MANAGER);
        cleanManagementService.createMember("2423001", "권혁일", "2027_A");
        cleanManagementService.createMember("2423002", "김규민", "2027_A");
        cleanManagementService.createMember("2423003", "김근형", "2027_A");
        cleanManagementService.createMember("2423005", "김민규", "2027_A");
        cleanManagementService.createMember("2423006", "김민석", "2027_A");
        cleanManagementService.createMember("2423008", "김성관", "2027_A");
        cleanManagementService.createMember("2423009", "김성식", "2027_A");

        assertThat(schoolClass.getClassMembers().size()).isEqualTo(7);
    }
}
