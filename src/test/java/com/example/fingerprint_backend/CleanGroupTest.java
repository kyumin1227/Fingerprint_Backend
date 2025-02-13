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

    @Test
    void setGroupMember() {
        CleanMember member = new CleanMember("2423001", "권혁일", classroom, CleanAttendanceStatus.ATTENDING, CleanRole.MEMBER);
        cleanGroup.appendMember(member);
    }
}
