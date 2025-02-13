package com.example.fingerprint_backend;

import com.example.fingerprint_backend.entity.Classroom;
import com.example.fingerprint_backend.entity.CleanGroup;
import com.example.fingerprint_backend.entity.CleanMember;
import com.example.fingerprint_backend.types.CleanAttendanceStatus;
import com.example.fingerprint_backend.types.CleanRole;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;

public class CleanGroupTest {

    private static CleanGroup cleanGroup;
    private static Classroom classroom;

    @BeforeAll
    static void setup() {
        cleanGroup = new CleanGroup(1L, 4, new ArrayList<>());
        classroom = new Classroom(1L, "2027_A", new ArrayList<>(), new HashSet<>());
    }

//    @Test
//    void createCleanGroup() {
//        CleanService.createCleanGroup
//    }

    @Test
    void setGroupMember() {
        CleanMember member = new CleanMember("2423001", "권혁일", classroom, CleanAttendanceStatus.ATTENDING, CleanRole.MEMBER);
        cleanGroup.appendMember(member);
    }
}
