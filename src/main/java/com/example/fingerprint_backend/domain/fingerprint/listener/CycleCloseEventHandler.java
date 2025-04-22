package com.example.fingerprint_backend.domain.fingerprint.listener;

import com.example.fingerprint_backend.domain.fingerprint.entity.AttendanceCycle;
import com.example.fingerprint_backend.domain.fingerprint.entity.DailyStats;
import com.example.fingerprint_backend.domain.fingerprint.entity.OutingCycle;
import com.example.fingerprint_backend.domain.fingerprint.event.AttendanceCycleCloseEvent;
import com.example.fingerprint_backend.domain.fingerprint.event.OutingCycleCloseEvent;
import com.example.fingerprint_backend.domain.fingerprint.service.stats.StatsApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CycleCloseEventHandler {

    private final StatsApplicationService statsApplicationService;

    /**
     * 출석 사이클 종료 이벤트 핸들러
     *
     * @param event AttendanceCycleCloseEvent
     */
//    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    @EventListener
    public void handleCycleClosed(AttendanceCycleCloseEvent event) {

        AttendanceCycle attendanceCycle = event.getAttendanceCycle();

        List<DailyStats> dailyStatsList = statsApplicationService.getOrCreateDailyStatsInRange(attendanceCycle);

        for (DailyStats dailyStats : dailyStatsList) {
            Long durationTime = statsApplicationService.getDailyDurationTimeForCycle(attendanceCycle, dailyStats.getEffectiveDate());
            dailyStats.updateStayDuration(durationTime);
        }

//        TODO : 주간 통계, 월간 통계 비동기 이벤트 발행

    }

    /**
     * 외출 사이클 종료 이벤트 핸들러
     *
     * @param event OutingCycleCloseEvent
     */
//    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    @EventListener
    public void handleCycleClose(OutingCycleCloseEvent event) {

        OutingCycle outingCycle = event.getOutingCycle();

        List<DailyStats> dailyStatsList = statsApplicationService.getOrCreateDailyStatsInRange(outingCycle);

        for (DailyStats dailyStats : dailyStatsList) {
            Long outingTime = statsApplicationService.getDailyOutingTimeForCycle(outingCycle, dailyStats.getEffectiveDate());
            dailyStats.updateOutDuration(outingTime);
        }
    }

}
