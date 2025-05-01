package com.example.fingerprint_backend.domain.fingerprint.listener;

import com.example.fingerprint_backend.domain.fingerprint.entity.AttendanceCycle;
import com.example.fingerprint_backend.domain.fingerprint.entity.DailyStats;
import com.example.fingerprint_backend.domain.fingerprint.event.DailyStatsUpdateEvent;
import com.example.fingerprint_backend.domain.fingerprint.event.MonthlyStatsUpdateEvent;
import com.example.fingerprint_backend.domain.fingerprint.event.WeeklyStatsUpdateEvent;
import com.example.fingerprint_backend.domain.fingerprint.service.stats.StatsApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StatsUpdateEventHandler {

    private final StatsApplicationService statsApplicationService;

    /**
     * 출석 사이클 종료 이벤트 핸들러
     *
     * <p>출석 사이클이 종료되면 일간 통계의 체류 시간, 외출 시간을 업데이트</p>
     *
     * @param event AttendanceCycleCloseEvent
     */
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleDailyStatsUpdate(DailyStatsUpdateEvent event) {

        AttendanceCycle attendanceCycle = event.getAttendanceCycle();

        List<DailyStats> dailyStatsList = statsApplicationService.getOrCreateDailyStatsInCycle(attendanceCycle);

        statsApplicationService.updateDailyStats(attendanceCycle, dailyStatsList);

    }

    /**
     * 주간 통계 업데이트 이벤트 핸들러
     *
     * <p>주간 통계의 체류 시간, 외출 시간을 업데이트</p>
     *
     * @param event WeeklyStatsUpdateEvent
     */
    @EventListener
    @Transactional
    public void handleWeeklyStatsUpdate(WeeklyStatsUpdateEvent event) {

        statsApplicationService.updateWeeklyStats(event.studentNumber(), event.effectiveDate());

    }

    /**
     * 월간 통계 업데이트 이벤트 핸들러
     *
     * <p>월간 통계의 체류 시간, 외출 시간을 업데이트</p>
     *
     * @param event MonthlyStatsUpdateEvent
     */
    @EventListener
    @Transactional
    public void handleMonthlyStatsUpdate(MonthlyStatsUpdateEvent event) {

        statsApplicationService.updateMonthlyStats((event.studentNumber()), event.effectiveDate());

    }

}
