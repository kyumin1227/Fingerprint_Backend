package com.example.fingerprint_backend.domain.fingerprint.event;

import com.example.fingerprint_backend.domain.fingerprint.entity.AttendanceCycle;

public class AttendanceCycleCloseEvent {

    private final AttendanceCycle attendanceCycle;

    public AttendanceCycleCloseEvent(AttendanceCycle attendanceCycle) {
        this.attendanceCycle = attendanceCycle;
    }

    public AttendanceCycle getAttendanceCycle() {
        return attendanceCycle;
    }

}
