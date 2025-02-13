package com.example.fingerprint_backend;

import com.example.fingerprint_backend.entity.CleanGroup;
import com.example.fingerprint_backend.entity.CleanMember;
import com.example.fingerprint_backend.service.CleanGroupService;
import com.example.fingerprint_backend.service.CleanManagementService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest
@ContextConfiguration(initializers = DotenvTestInitializer.class)
@Transactional
public class CleanGroupServiceTest {

    @Autowired private CleanGroupService cleanGroupService;
    @Autowired private CleanManagementService cleanManagementService;

    private static CleanMember member1;
    private static CleanMember member2;
    private static CleanMember member3;
    private static CleanMember member4;
    private static CleanMember member5;
    private static CleanMember member6;
    private static CleanMember member7;
    private static CleanMember member8;


    @BeforeEach
    void setup() {
        cleanManagementService.createClassroom("2027_A");
        member1 = cleanManagementService.createMember("2423001", "권혁일", "2027_A");
        member2 = cleanManagementService.createMember("2423002", "김규민", "2027_A");
        member3 = cleanManagementService.createMember("2423003", "김근형", "2027_A");
        member4 = cleanManagementService.createMember("2423005", "김민규", "2027_A");
        member5 = cleanManagementService.createMember("2423006", "김민석", "2027_A");
        member6 = cleanManagementService.createMember("2423007", "김민정", "2027_A");
        member7 = cleanManagementService.createMember("2423008", "김성관", "2027_A");
        member8 = cleanManagementService.createMember("2423009", "김성식", "2027_A");
    }

    @DisplayName("청소 그룹을 생성한다.")
    @Test
    void create() {
        CleanGroup group = cleanGroupService.createGroup(4, new HashSet<>(Set.of(member1, member2, member3, member4)));

        assertThat(group).isNotNull();
    }

    @Test
    void createError() {
        assertThatCode(() -> cleanGroupService.createGroup(4, new HashSet<>(Set.of(member1, member2, member3, member4, member5))))
                .isInstanceOf(IllegalArgumentException.class)
                .as("그룹에 최대 인원 보다 많은 인원으로 생성 시도")
                .hasMessage("멤버 수가 그룹의 최대 인원을 초과합니다.");

        CleanGroup group = cleanGroupService.createGroup(4, new HashSet<>(Set.of(member1, member2, member3, member4)));

        assertThatCode(() -> cleanGroupService.appendMember(group, member1))
                .isInstanceOf(IllegalArgumentException.class)
                .as("그룹에 최대 인원 상태에서 멤버 추가 시도")
                .hasMessage("더 이상 그룹에 추가할 수 없습니다.");

        cleanGroupService.removeMember(group, member4);

        assertThat(group.getMembers().size()).as("한명 제거한 이후 그룹 멤버 수").isEqualTo(3);

        assertThatCode(() -> cleanGroupService.removeMember(group, member4))
                .isInstanceOf(IllegalArgumentException.class)
                .as("그룹에 없는 멤버 삭제 시도")
                .hasMessage("그룹에 존재하지 않는 멤버입니다.");

        assertThatCode(() -> cleanGroupService.appendMember(group, member1))
                .isInstanceOf(IllegalArgumentException.class)
                .as("그룹에 이미 있는 멤버 추가 시도")
                .hasMessage("이미 그룹에 존재하는 멤버입니다.");
    }

    @Test
    void deleteGroup() {
        CleanGroup group = cleanGroupService.createGroup(4, new HashSet<>(Set.of(member1, member2, member3, member4)));

        cleanGroupService.deleteGroup(group);

        assertThatCode(() -> cleanGroupService.removeMember(group, member1))
                .isInstanceOf(IllegalArgumentException.class)
                .as("삭제된 그룹에서 멤버 삭제 시도")
                .hasMessage("존재하지 않는 청소 그룹입니다.");
    }
}
