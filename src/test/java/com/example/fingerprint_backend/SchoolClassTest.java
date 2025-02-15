package com.example.fingerprint_backend;

import com.example.fingerprint_backend.entity.SchoolClass;
import com.example.fingerprint_backend.entity.CleanArea;
import com.example.fingerprint_backend.entity.CleanMember;
import com.example.fingerprint_backend.types.CleanAttendanceStatus;
import com.example.fingerprint_backend.types.CleanRole;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

public class SchoolClassTest {

    private static SchoolClass schoolClass;

    @BeforeAll
    static void setup() {
        schoolClass = new SchoolClass(1L, "2027_A", new ArrayList<>(), new HashSet<>(), new HashSet<>());
    }

    @DisplayName("반 생성")
    @Test
    void createClassroom() {
        assertThat(schoolClass).isNotNull();
    }

    @DisplayName("동일한 학생이 있다면 추가하지 않음")
    @Test
    void setMember() {
        CleanMember member1 = new CleanMember("2423001", "권혁일", schoolClass, CleanAttendanceStatus.ATTENDING, CleanRole.MEMBER);
        CleanMember member2 = new CleanMember("2423002", "김규민", schoolClass, CleanAttendanceStatus.ATTENDING, CleanRole.MEMBER);
        schoolClass.appendMember(member1);
        schoolClass.appendMember(member2);
        schoolClass.appendMember(member1);    // 동일한 멤버 추가

        assertThat(schoolClass.getMembers().size()).isEqualTo(2);
    }

    @DisplayName("반의 학생 삭제")
    @Test
    void removeMember() {
        CleanMember member1 = new CleanMember("2423001", "권혁일", schoolClass, CleanAttendanceStatus.ATTENDING, CleanRole.MEMBER);
        CleanMember member2 = new CleanMember("2423002", "김규민", schoolClass, CleanAttendanceStatus.ATTENDING, CleanRole.MEMBER);
        schoolClass.appendMember(member1);
        schoolClass.appendMember(member2);
        schoolClass.removeMember(member1);

        assertThat(schoolClass.getMembers().size()).isEqualTo(1);
    }

    @DisplayName("반의 청소 구역 설정")
    @Test
    void setCleanArea() {
        CleanArea cleanArea = new CleanArea(1L, "창조관 405호", new HashSet<>());
        schoolClass.appendArea(cleanArea);
        schoolClass.appendArea(cleanArea);

        assertThat(schoolClass.getAreas().size()).isEqualTo(1);
    }

    @DisplayName("반의 청소 구역 삭제")
    @Test
    void removeCleanArea() {
        CleanArea cleanArea = new CleanArea(1L, "창조관 405호", new HashSet<>());
        schoolClass.appendArea(cleanArea);
        schoolClass.removeArea(cleanArea);

        assertThat(schoolClass.getAreas().size()).isEqualTo(0);
    }
}
