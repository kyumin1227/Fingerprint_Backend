package com.example.fingerprint_backend;

import com.example.fingerprint_backend.entity.CleanMember;
import com.example.fingerprint_backend.service.CleanManagementService;
import com.example.fingerprint_backend.types.CleanAttendanceStatus;
import com.example.fingerprint_backend.types.CleanRole;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest
@ContextConfiguration(initializers = DotenvTestInitializer.class)
@Transactional
public class ClassroomServiceTest {

    @Autowired private CleanManagementService cleanManagementService;

    @BeforeEach
    void setup() {
        cleanManagementService.createClassroom("2027_A");
        cleanManagementService.createMember("2423001", "권혁일", "2027_A", CleanAttendanceStatus.ATTENDING, CleanRole.MEMBER);
        cleanManagementService.createMember("2423002", "김규민", "2027_A", CleanAttendanceStatus.ABSENT, CleanRole.MEMBER);
        cleanManagementService.createMember("2423003", "김근형", "2027_A", CleanAttendanceStatus.ATTENDING, CleanRole.MEMBER);
        cleanManagementService.createMember("2423005", "김민규", "2027_A", CleanAttendanceStatus.ATTENDING, CleanRole.MEMBER);
        cleanManagementService.createMember("2423006", "김민석", "2027_A", CleanAttendanceStatus.ATTENDING, CleanRole.MEMBER);
        cleanManagementService.createMember("2423007", "김민정", "2027_A", CleanAttendanceStatus.ATTENDING, CleanRole.LEADER);
    }

    @DisplayName("같은 이름의 반을 중복 생성.")
    @Test
    void createError() {
        assertThatCode(() -> cleanManagementService.createClassroom("2027_A")).isInstanceOf(IllegalStateException.class).hasMessage("이미 존재하는 반 이름입니다.");
    }

    @DisplayName("반 이름으로 학생들을 가져온다.")
    @Test
    void checkClassroom() {
        List<CleanMember> cleanMemberList = cleanManagementService.getMembersByClassroomName("2027_A");

        assertThat(cleanMemberList.size()).isEqualTo(6);
    }

//    @DisplayName("청소 역할이 Append 인 학생들만 가져온다.")
//    @Test
//    void name() {
//        List<CleanMember> cleanMemberList = cleanService.getMembersByClassroomNameAndCleanMemberStatus("2027_A", "MEMBER");
//
//        assertThat(cleanMemberList.size()).isEqualTo(5);
//    }
}
