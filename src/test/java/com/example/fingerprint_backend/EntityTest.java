package com.example.fingerprint_backend;

import com.example.fingerprint_backend.entity.*;
import com.example.fingerprint_backend.types.CleanRole;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

public class EntityTest {

    private static SchoolClass schoolClass;
    private static CleanMember member1;
    private static CleanMember member2;
    private static CleanMember member3;
    private static CleanMember member4;
    private static CleanMember member5;
    private static CleanMember member6;
    private static CleanMember member7;
    private static CleanMember member8;
    private static CleanArea cleanArea1;
    private static CleanArea cleanArea2;

    @BeforeAll
    static void setup() {
        schoolClass = new SchoolClass("2027_A");
        member1 = new CleanMember("2423001", "권혁일", schoolClass);
        member2 = new CleanMember("2423002", "김규민", schoolClass);
        member3 = new CleanMember("2423003", "김근형", schoolClass);
        member4 = new CleanMember("2423005", "김민규", schoolClass);
        member5 = new CleanMember("2423006", "김민석", schoolClass);
        member6 = new CleanMember("2423007", "김민정", schoolClass, CleanRole.MANAGER);
        member7 = new CleanMember("2423008", "김성관", schoolClass);
        member8 = new CleanMember("2423009", "김성식", schoolClass);
        cleanArea1 = new CleanArea("창조관 405호", schoolClass, new HashSet<>(), 0);
        cleanArea2 = new CleanArea("창조관 406호", schoolClass, new HashSet<>(), 0);
    }

//    @DisplayName("반 생성 테스트")
//    @Test
//    void checkSchoolClass() {
//        assertThat(schoolClass.getName()).as("반 이름 확인").isEqualTo("2027_A");
//        assertThat(schoolClass.getMembers().size()).as("인원 수 확인").isEqualTo(8);
//        assertThat(schoolClass.getAreas().size()).as("청소 구역 수 확인").isEqualTo(2);
////        assertThat(schoolClass.getManager()).as("매니저 확인").isEqualTo(member6);
//    }

    @DisplayName("멤버 생성 테스트")
    @Test
    void checkMember() {
        assertThat(member1.getStudentNumber()).as("학번 확인").isEqualTo("2423001");
        assertThat(member1.getName()).as("이름 확인").isEqualTo("권혁일");
        assertThat(member1.getSchoolClass()).as("반 확인").isEqualTo(schoolClass);
        assertThat(member1.getCleanRole()).as("역할 확인").isEqualTo(CleanRole.MEMBER);

        assertThat(member6.getStudentNumber()).as("학번 확인").isEqualTo("2423007");
        assertThat(member6.getName()).as("이름 확인").isEqualTo("김민정");
        assertThat(member6.getSchoolClass()).as("반 확인").isEqualTo(schoolClass);
        assertThat(member6.getCleanRole()).as("역할 확인").isEqualTo(CleanRole.MANAGER);
    }

    @DisplayName("청소 구역 생성 테스트")
    @Test
    void checkArea() {
        assertThat(cleanArea1.getName()).as("이름 확인").isEqualTo("창조관 405호");
        assertThat(cleanArea1.getSchoolClass()).as("반 확인").isEqualTo(schoolClass);
        assertThat(cleanArea1.getDays().size()).as("요일 확인").isEqualTo(0);
    }

    @DisplayName("그룹 테스트")
    @Test
    void checkGroup() {
        CleanGroup cleanGroup = new CleanGroup(schoolClass, 4, new HashSet<>());

        assertThat(cleanGroup.getSchoolClass()).as("반 확인").isEqualTo(schoolClass);
        assertThat(cleanGroup.getMembers().size()).as("인원 수 확인").isEqualTo(0);

        cleanGroup.appendMember(member1);

        assertThatCode(() -> cleanGroup.appendMember(member1)).as("멤버 중복 추가").isInstanceOf(IllegalArgumentException.class).hasMessage("이미 그룹에 존재하는 멤버입니다.");

        cleanGroup.appendMember(member2);
        cleanGroup.appendMember(member3);
        cleanGroup.appendMember(member4);

        assertThatCode(() -> cleanGroup.appendMember(member5)).as("최대 인원 상태에서 멤버 추가").isInstanceOf(IllegalArgumentException.class).hasMessage("더 이상 그룹에 추가할 수 없습니다.");

        assertThat(cleanGroup.getMembers().size()).as("멤버 중복 추가 후 인원 수 확인").isEqualTo(4);

        cleanGroup.setCleaned(true);

        assertThat(cleanGroup.isCleaned()).as("청소 여부 확인").isTrue();
        assertThat(member1.getCleaningCount()).as("청소 횟수 확인").isEqualTo(1);
    }

    @DisplayName("스케줄 테스트")
    @Test
    void checkSchedule() {
        CleanSchedule cleanSchedule = new CleanSchedule(LocalDate.of(2025, 2, 16), cleanArea1, schoolClass);

        assertThat(cleanSchedule.getDate()).as("날짜 확인").isEqualTo(LocalDate.of(2025, 2, 16));

        CleanGroup cleanGroup = new CleanGroup(schoolClass, 2, new HashSet<>());
        cleanGroup.appendMember(member5);
        cleanGroup.appendMember(member6);

        cleanSchedule.setCleanGroup(cleanGroup);
        cleanSchedule.updateGroupCleaningCount();

        assertThat(cleanGroup.isCleaned()).as("청소 여부 확인").isTrue();
        assertThat(member5.getCleaningCount()).as("청소 횟수 확인").isEqualTo(1);
    }
}
