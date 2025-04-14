package com.example.fingerprint_backend;

import com.example.fingerprint_backend.entity.ClassClosingTime;
import com.example.fingerprint_backend.entity.LogEntity;
import com.example.fingerprint_backend.entity.MemberEntity;
import com.example.fingerprint_backend.entity.SchoolClass;
import com.example.fingerprint_backend.repository.MemberRepository;
import com.example.fingerprint_backend.repository.SchoolClassRepository;
import com.example.fingerprint_backend.service.LogService;
import com.example.fingerprint_backend.types.LogAction;
import com.example.fingerprint_backend.types.MemberRole;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest
@Transactional
public class FingerprintTest {

    @Autowired
    private LogService logService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private SchoolClassRepository schoolClassRepository;


    @DisplayName("정상적으로 로그 생성")
    @Test
    void createLog() {
        // given
        MemberEntity member = new MemberEntity();
        member.setStudentNumber("20230001");
        member.setName("홍길동");
        memberRepository.save(member);

        String studentNumber = "20230001";
        LogAction logAction = LogAction.등교;

        // when
        LogEntity log = logService.createLog(studentNumber, logAction);

        // then
        assertThat(log.getStudentNumber()).as("학번 검증").isEqualTo(studentNumber);
        assertThat(log.getAction()).as("액션 검증").isEqualTo(logAction);
    }

    @DisplayName("존재하지 않는 학번으로 로그를 생성할 경우 오류 발생")
    @Test
    void notExistStudent() {
        // given
        String studentNumber = "20230001";
        LogAction logAction = LogAction.등교;

        // when
        try {
            logService.createLog(studentNumber, logAction);
        } catch (IllegalArgumentException e) {
            assert e.getMessage().equals("해당 학번의 학생이 존재하지 않습니다.");
        }
    }

    @DisplayName("로그가 중복이 될 경우 오류 발생")
    @Test
    void duplicateLog() {
        // given
        MemberEntity member = new MemberEntity();
        member.setStudentNumber("20230001");
        member.setName("홍길동");
        memberRepository.save(member);

        String studentNumber = "20230001";
        LogAction logAction = LogAction.등교;

        // when
        logService.createLog(studentNumber, logAction);

        // then
        assertThatCode(() -> {
            logService.createLog(studentNumber, logAction);
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 등록된 로그입니다.");
    }

    @DisplayName("정상적으로 문 닫힘 생성")
    @Test
    void createClassClose() {
        // before
//        멤버 및 학교 반 생성
        MemberEntity member = new MemberEntity();
        member.setStudentNumber("20230001");
        member.setName("홍길동");
        member.addRole(MemberRole.KEY);

        SchoolClass schoolClass = schoolClassRepository.save(new SchoolClass("1반"));

        schoolClass.appendMember(member);
        member.setSchoolClass(schoolClass);

        memberRepository.save(member);

        // given
        String closingMember = "20230001";
        LocalDateTime closingTime = LocalDateTime.now();

        // when
        ClassClosingTime classClosingTime = logService.createClosingTime(closingTime, closingMember);

        // then
        assertThat(classClosingTime.getClosingMember()).as("문 닫힘 담당자 검증").isEqualTo(closingMember);
    }

    @DisplayName("열쇠 담당자가 아닌 경우 문 닫힘 생성 시 오류 발생")
    @Test
    void noKeyDoClassClose() {
        // before
//        멤버 및 학교 반 생성
        MemberEntity member = new MemberEntity();
        member.setStudentNumber("20230001");
        member.setName("홍길동");

        SchoolClass schoolClass = schoolClassRepository.save(new SchoolClass("1반"));

        schoolClass.appendMember(member);
        member.setSchoolClass(schoolClass);

        memberRepository.save(member);

        // given
        String closingMember = "20230001";
        LocalDateTime closingTime = LocalDateTime.now();

        // when
        assertThatCode(() -> {
            logService.createClosingTime(closingTime, closingMember);
        })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("열쇠 담당자만 문을 닫을 수 있습니다.");
    }

    @DisplayName("5분 이내에 문을 연속으로 닫을 경우 오류 발생")
    @Test
    void duplicateClose() {
        // before
//        멤버 및 학교 반 생성
        MemberEntity member = new MemberEntity();
        member.setStudentNumber("20230001");
        member.setName("홍길동");
        member.addRole(MemberRole.KEY);

        SchoolClass schoolClass = schoolClassRepository.save(new SchoolClass("1반"));

        schoolClass.appendMember(member);
        member.setSchoolClass(schoolClass);

        memberRepository.save(member);

        // given
        String closingMember = "20230001";
        LocalDateTime closingTime = LocalDateTime.now();

        logService.createClosingTime(closingTime, closingMember);

        // when
        assertThatCode(() -> {
            logService.createClosingTime(closingTime, closingMember);
        })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 문이 닫혀있습니다.");
    }

}
