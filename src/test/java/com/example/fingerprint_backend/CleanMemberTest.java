package com.example.fingerprint_backend;

import com.example.fingerprint_backend.entity.Classroom;
import com.example.fingerprint_backend.entity.CleanMember;
import com.example.fingerprint_backend.types.CleanAttendanceStatus;
import com.example.fingerprint_backend.types.CleanRole;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

public class CleanMemberTest {

    private static CleanMember member;

    @BeforeAll
    static void setup() {
        Classroom classroom = new Classroom(1L, "2027_A", new ArrayList<>(), new HashSet<>());
        member = new CleanMember("2423002", "김규민", classroom, CleanAttendanceStatus.ATTENDING, CleanRole.MEMBER);

        assertThat(member).isNotNull();
    }

    @DisplayName("멤버의 반을 변경")
    @Test
    void changeClassroom() {
        Classroom classroom1 = new Classroom(1L, "2027_A", new ArrayList<>(), new HashSet<>());
        Classroom classroom2 = new Classroom(2L, "2027_B", new ArrayList<>(), new HashSet<>());

        member.setClassroom(classroom1);
        member.setClassroom(classroom2);

        assertThat(classroom1.getMembers().size()).as("이전 반의 멤버 삭제 여부 확인").isEqualTo(0);
        assertThat(classroom2.getMembers().size()).as("현재 반의 멤버 추가 여부 확인").isEqualTo(1);
        assertThat(member.getClassroom()).as("현재 멤버의 반 확인").isEqualTo(classroom2);
    }
}
