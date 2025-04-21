package com.example.fingerprint_backend.domain.fingerprint.listener;

import com.example.fingerprint_backend.domain.fingerprint.event.AttendanceCycleCloseEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class AttendanceCycleCloseEventHandler {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCycleClosed(AttendanceCycleCloseEvent event) {

//        TODO : 사이클 종료 시점에 통계 생성 또는 갱신

    }

}
