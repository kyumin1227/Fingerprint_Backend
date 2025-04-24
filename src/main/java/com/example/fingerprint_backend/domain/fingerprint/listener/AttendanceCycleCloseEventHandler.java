package com.example.fingerprint_backend.domain.fingerprint.listener;

import com.example.fingerprint_backend.domain.fingerprint.entity.AttendanceCycle;
import com.example.fingerprint_backend.domain.fingerprint.entity.DailyStats;
import com.example.fingerprint_backend.domain.fingerprint.event.AttendanceCycleCloseEvent;
import com.example.fingerprint_backend.domain.fingerprint.event.WeeklyStatsUpdateEvent;
import com.example.fingerprint_backend.domain.fingerprint.service.stats.StatsApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AttendanceCycleCloseEventHandler {

    private final StatsApplicationService statsApplicationService;
    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * 출석 사이클 종료 이벤트 핸들러
     *
     * <p>출석 사이클이 종료되면 일간 통계의 체류 시간, 외출 시간을 업데이트</p>
     *
     * @param event AttendanceCycleCloseEvent
     */
//    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    @EventListener
    public void handleAttendanceCycleClosed(AttendanceCycleCloseEvent event) {

        AttendanceCycle attendanceCycle = event.getAttendanceCycle();

        List<DailyStats> dailyStatsList = statsApplicationService.getOrCreateDailyStatsInRange(attendanceCycle);

        for (DailyStats dailyStats : dailyStatsList) {
            Long durationTime = statsApplicationService.getDailyDurationTimeForCycle(attendanceCycle, dailyStats.getEffectiveDate());
            dailyStats.updateStayDuration(durationTime);
            dailyStats.updateOutDuration(attendanceCycle.getTotalOutingDuration());
        }

//        TODO : 주간 통계, 월간 통계 비동기 이벤트 발행

    }

}
