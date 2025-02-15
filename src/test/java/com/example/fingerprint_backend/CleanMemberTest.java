package com.example.fingerprint_backend;

import com.example.fingerprint_backend.entity.SchoolClass;
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
        SchoolClass schoolClass = new SchoolClass(1L, "2027_A", new ArrayList<>(), new HashSet<>(), new HashSet<>());
        member = new CleanMember("2423002", "김규민", schoolClass, CleanAttendanceStatus.ATTENDING, CleanRole.MEMBER);

        assertThat(member).isNotNull();
    }

    @DisplayName("멤버의 반을 변경")
    @Test
    void changeClassroom() {
        SchoolClass schoolClass1 = new SchoolClass(1L, "2027_A", new ArrayList<>(), new HashSet<>(), new HashSet<>());
        SchoolClass schoolClass2 = new SchoolClass(2L, "2027_B", new ArrayList<>(), new HashSet<>(), new HashSet<>());

        member.setSchoolClass(schoolClass1);
        member.setSchoolClass(schoolClass2);

        assertThat(schoolClass1.getMembers().size()).as("이전 반의 멤버 삭제 여부 확인").isEqualTo(0);
        assertThat(schoolClass2.getMembers().size()).as("현재 반의 멤버 추가 여부 확인").isEqualTo(1);
        assertThat(member.getSchoolClass()).as("현재 멤버의 반 확인").isEqualTo(schoolClass2);
    }
}
