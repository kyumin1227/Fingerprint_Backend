package com.example.fingerprint_backend.domain.fingerprint.event;

import com.example.fingerprint_backend.domain.fingerprint.entity.AttendanceCycle;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AttendanceCycleCloseEvent {

    private final AttendanceCycle attendanceCycle;

}
