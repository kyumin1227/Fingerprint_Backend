package com.example.fingerprint_backend;

import com.example.fingerprint_backend.entity.Classroom;
import com.example.fingerprint_backend.entity.CleanMember;
import com.example.fingerprint_backend.types.CleanAttendanceStatus;
import com.example.fingerprint_backend.types.CleanRole;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ClassroomTest {

    private static Classroom classroom;

    @BeforeAll
    static void setup() {
        classroom = new Classroom(1L, "2027_A", new ArrayList<>());
    }

    @DisplayName("반 생성")
    @Test
    @Order(1)
    void createClassroom() {
        assertThat(classroom).isNotNull();
    }

    @DisplayName("동일한 학생이 있다면 추가하지 않음")
    @Test
    @Order(2)
    void setMember() {
        CleanMember member1 = new CleanMember("2423001", "권혁일", classroom, CleanAttendanceStatus.ATTENDING, CleanRole.MEMBER);
        CleanMember member2 = new CleanMember("2423002", "김규민", classroom, CleanAttendanceStatus.ATTENDING, CleanRole.MEMBER);
        classroom.setMember(member1);
        classroom.setMember(member2);
        classroom.setMember(member1);    // 동일한 멤버 추가

        assertThat(classroom.getMembers().size()).isEqualTo(2);
    }

    @DisplayName("반의 학생 삭제")
    @Test
    @Order(3)
    void removeMember() {
        CleanMember member1 = new CleanMember("2423001", "권혁일", classroom, CleanAttendanceStatus.ATTENDING, CleanRole.MEMBER);
        classroom.removeMember(member1);

        assertThat(classroom.getMembers().size()).isEqualTo(1);
    }
}
