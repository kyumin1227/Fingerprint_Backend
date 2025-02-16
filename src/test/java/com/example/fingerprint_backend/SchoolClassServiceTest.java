//package com.example.fingerprint_backend;
//
//import com.example.fingerprint_backend.entity.CleanArea;
//import com.example.fingerprint_backend.entity.CleanMember;
//import com.example.fingerprint_backend.service.CleanManagementService;
//import com.example.fingerprint_backend.types.CleanRole;
//import jakarta.transaction.Transactional;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ContextConfiguration;
//
//import java.util.Set;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatCode;
//
//@SpringBootTest
//@ContextConfiguration(initializers = DotenvTestInitializer.class)
//@Transactional
//public class SchoolClassServiceTest {
//
//    @Autowired
//    private CleanManagementService cleanManagementService;
//
//    @BeforeEach
//    void setup() {
//        cleanManagementService.createSchoolClass("2027_A");
//        cleanManagementService.createMember("2423001", "권혁일", "2027_A", CleanRole.MEMBER);
//        cleanManagementService.createMember("2423002", "김규민", "2027_A", CleanRole.MEMBER);
//        cleanManagementService.createMember("2423003", "김근형", "2027_A", CleanRole.MEMBER);
//        cleanManagementService.createMember("2423005", "김민규", "2027_A", CleanRole.MEMBER);
//        cleanManagementService.createMember("2423006", "김민석", "2027_A", CleanRole.MEMBER);
//        cleanManagementService.createMember("2423007", "김민정", "2027_A", CleanRole.MANAGER);
//    }
//
//    @DisplayName("같은 이름의 반을 중복 생성.")
//    @Test
//    void createSchoolClassError() {
//        assertThatCode(() -> cleanManagementService.createSchoolClass("2027_A")).isInstanceOf(IllegalStateException.class).hasMessage("이미 존재하는 반 이름입니다.");
//    }
//
//    @DisplayName("같은 학번의 학생을 중복 생성.")
//    @Test
//    void createCleanMemberError() {
//        assertThatCode(() -> cleanManagementService.createMember("2423001", "권혁일", "2027_A", CleanRole.MEMBER)).isInstanceOf(IllegalStateException.class).hasMessage("이미 존재하는 학번입니다.");
//    }
//
//    @DisplayName("청소 구역 생성.")
//    @Test
//    void createCleanArea() {
//        CleanArea cleanArea = cleanManagementService.createArea("창조관 405호");
//
//        assertThat(cleanArea).isNotNull();
//    }
//
//    @DisplayName("같은 이름의 청소 구역 중복 생성")
//    @Test
//    void createCleanAreaError() {
//        CleanArea cleanArea = cleanManagementService.createArea("창조관 405호");
//
//        assertThatCode(() -> cleanManagementService.createArea("창조관 405호")).isInstanceOf(IllegalStateException.class).hasMessage("이미 존재하는 청소 구역 이름입니다.");
//    }
//
//    @DisplayName("반 이름으로 학생들을 가져온다.")
//    @Test
//    void getMembersByClassname() {
//        Set<CleanMember> cleanMemberList = cleanManagementService.getMembersBySchoolClassName("2027_A");
//
//        assertThat(cleanMemberList.size()).isEqualTo(6);
//    }
//
//    @DisplayName("청소 구역 설정")
//    @Test
//    void setCleanArea() {
//        CleanArea area = cleanManagementService.createArea("창조관 405호");
//        cleanManagementService.setCleanArea("2027_A", "창조관 405호");
//
//        assertThat(cleanManagementService.getSchoolClassByName("2027_A").getAreas().contains(area)).isTrue();
//    }
//
////    @DisplayName("청소 역할이 Append 인 학생들만 가져온다.")
////    @Test
////    void name() {
////        List<CleanMember> cleanMemberList = cleanManagementService.getMembersBySchoolClassNameAndCleanMemberStatus("2027_A");
////
////        assertThat(cleanMemberList.size()).isEqualTo(5);
////    }
//}
