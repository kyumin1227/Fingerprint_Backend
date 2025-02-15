package com.example.fingerprint_backend;

import com.example.fingerprint_backend.entity.SchoolClass;
import com.example.fingerprint_backend.entity.CleanGroup;
import com.example.fingerprint_backend.entity.CleanMember;
import com.example.fingerprint_backend.types.CleanAttendanceStatus;
import com.example.fingerprint_backend.types.CleanRole;
import org.junit.jupiter.api.Test;

public class CleanGroupTest {

    private static CleanGroup cleanGroup;
    private static SchoolClass schoolClass;

    @Test
    void setGroupMember() {
        CleanMember member = new CleanMember("2423001", "권혁일", schoolClass, CleanAttendanceStatus.ATTENDING, CleanRole.MEMBER);
        cleanGroup.appendMember(member);
    }
}
