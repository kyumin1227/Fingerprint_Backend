package com.example.fingerprint_backend;

import com.example.fingerprint_backend.entity.Classroom;
import com.example.fingerprint_backend.entity.CleanArea;
import com.example.fingerprint_backend.entity.CleanMember;
import com.example.fingerprint_backend.types.CleanAttendanceStatus;
import com.example.fingerprint_backend.types.CleanRole;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

public class ClassroomTest {

    private static Classroom classroom;

    @BeforeAll
    static void setup() {
        classroom = new Classroom(1L, "2027_A", new ArrayList<>(), new HashSet<>());
    }

    @DisplayName("반 생성")
    @Test
    void createClassroom() {
        assertThat(classroom).isNotNull();
    }

    @DisplayName("동일한 학생이 있다면 추가하지 않음")
    @Test
    void setMember() {
        CleanMember member1 = new CleanMember("2423001", "권혁일", classroom, CleanAttendanceStatus.ATTENDING, CleanRole.MEMBER);
        CleanMember member2 = new CleanMember("2423002", "김규민", classroom, CleanAttendanceStatus.ATTENDING, CleanRole.MEMBER);
        classroom.appendMember(member1);
        classroom.appendMember(member2);
        classroom.appendMember(member1);    // 동일한 멤버 추가

        assertThat(classroom.getMembers().size()).isEqualTo(2);
    }

    @DisplayName("반의 학생 삭제")
    @Test
    void removeMember() {
        CleanMember member1 = new CleanMember("2423001", "권혁일", classroom, CleanAttendanceStatus.ATTENDING, CleanRole.MEMBER);
        CleanMember member2 = new CleanMember("2423002", "김규민", classroom, CleanAttendanceStatus.ATTENDING, CleanRole.MEMBER);
        classroom.appendMember(member1);
        classroom.appendMember(member2);
        classroom.removeMember(member1);

        assertThat(classroom.getMembers().size()).isEqualTo(1);
    }

    @DisplayName("반의 청소 구역 설정")
    @Test
    void setCleanArea() {
        CleanArea cleanArea = new CleanArea(1L, "창조관 405호", new HashSet<>());
        classroom.appendArea(cleanArea);
        classroom.appendArea(cleanArea);

        assertThat(classroom.getAreas().size()).isEqualTo(1);
    }

    @DisplayName("반의 청소 구역 삭제")
    @Test
    void removeCleanArea() {
        CleanArea cleanArea = new CleanArea(1L, "창조관 405호", new HashSet<>());
        classroom.appendArea(cleanArea);
        classroom.removeArea(cleanArea);

        assertThat(classroom.getAreas().size()).isEqualTo(0);
    }
}
