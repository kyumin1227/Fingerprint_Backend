package com.example.fingerprint_backend.domain.fingerprint.service.classClosingTime;


import com.example.fingerprint_backend.TestMemberFactory;
import com.example.fingerprint_backend.domain.fingerprint.entity.ClassClosingTime;
import com.example.fingerprint_backend.domain.fingerprint.exception.LogException;
import com.example.fingerprint_backend.dto.GoogleRegisterDto;
import com.example.fingerprint_backend.dto.LoginResponse;
import com.example.fingerprint_backend.entity.MemberEntity;
import com.example.fingerprint_backend.entity.SchoolClass;
import com.example.fingerprint_backend.service.AuthService;
import com.example.fingerprint_backend.service.CleanManagementService;
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
class ClassClosingTimeApplicationServiceTest {

    @Autowired
    private CleanManagementService cleanManagementService;
    @Autowired
    private AuthService authService;
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
    }

    @DisplayName("정상적인 문 닫기")
    @Test
    void success1() {
        // given
        String studentNumber = "2423007";
        LocalDateTime localDateTime = LocalDateTime.of(2025, 5, 1, 18, 0);

        // when
        ClassClosingTime classClosingTime = classClosingTimeApplicationService.createClosingTime(localDateTime, studentNumber);

        // then
        assertThat(classClosingTime).as("문 닫기 성공").isNotNull();
        assertThat(classClosingTime.getClosingMember()).as("문 닫기 담당자 검증").isEqualTo(studentNumber);
    }

    @DisplayName("열쇠 담당자가 아닐 경우")
    @Test
    void error1() {
        // given
        String studentNumber = "2423002";
        LocalDateTime localDateTime = LocalDateTime.of(2025, 5, 1, 18, 0);

        // when
        assertThatCode(() -> classClosingTimeApplicationService.createClosingTime(localDateTime, studentNumber))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 권한이 없습니다.");
    }

    @DisplayName("학번이 존재하지 않을 경우")
    @Test
    void error2() {
        // given
        String studentNumber = "2423008";
        LocalDateTime localDateTime = LocalDateTime.of(2025, 5, 1, 18, 0);

        // when
        assertThatCode(() -> classClosingTimeApplicationService.createClosingTime(localDateTime, studentNumber))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 학번의 학생이 존재하지 않습니다.");
    }

    @DisplayName("5분 이내에 연속으로 문을 닫을 경우")
    @Test
    void error3() {
        // given
        String studentNumber = "2423007";
        LocalDateTime localDateTime = LocalDateTime.of(2025, 5, 1, 18, 0);

        // when
        classClosingTimeApplicationService.createClosingTime(localDateTime, studentNumber);

        // then
        assertThatCode(() -> classClosingTimeApplicationService.createClosingTime(localDateTime.plusMinutes(2), studentNumber))
                .isInstanceOf(LogException.class)
                .hasMessage("이미 문이 닫혀있습니다.");
    }
}