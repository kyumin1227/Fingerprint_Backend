package com.example.fingerprint_backend.domain.fingerprint.event;

import com.example.fingerprint_backend.domain.fingerprint.entity.AttendanceCycle;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 출석 사이클 종료 이벤트
 */
@Getter
@RequiredArgsConstructor
public class AttendanceCycleCloseEvent {

    private final AttendanceCycle attendanceCycle;

}
