package com.example.fingerprint_backend.domain.fingerprint.event;

import com.example.fingerprint_backend.domain.fingerprint.entity.OutingCycle;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 외출 사이클 종료 이벤트
 */
@Getter
@RequiredArgsConstructor
public class OutingCycleCloseEvent {

    private final OutingCycle outingCycle;

}
