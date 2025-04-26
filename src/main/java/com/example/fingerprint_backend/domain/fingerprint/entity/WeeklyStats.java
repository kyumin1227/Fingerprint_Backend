package com.example.fingerprint_backend.domain.fingerprint.entity;

import com.example.fingerprint_backend.domain.fingerprint.exception.StatsException;
import com.example.fingerprint_backend.domain.fingerprint.util.DatePolicy;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "weekly_stats", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"student_number", "start_date"})
})
public class WeeklyStats extends ContinuousStats {

    private static final Long MAX_DURATION = 7 * 24 * 60 * 60 * 1000L; // 24시간 * 7일

    @Builder
    public WeeklyStats(String studentNumber, LocalDate startDate) {
        super(studentNumber, startDate);
        if (!startDate.getDayOfWeek().equals(DayOfWeek.MONDAY)) {
            throw new StatsException("주간 통계는 월요일부터 시작해야 합니다.");
        }
    }

    /**
     * 총 체류 시간 재설정
     *
     * @param stayDuration 총 체류 시간 (밀리초 단위)
     */
    @Override
    public void setStayDuration(Long stayDuration) {
        if (stayDuration > MAX_DURATION) {
            throw new StatsException("체류 시간은 7일을 초과할 수 없습니다.");
        }
        super.setStayDuration(stayDuration);
    }

    /**
     * 총 외출 시간 재설정
     *
     * @param outDuration 총 외출 시간 (밀리초 단위)
     */
    @Override
    public void setOutDuration(Long outDuration) {
        if (outDuration > MAX_DURATION) {
            throw new StatsException("외출 시간은 7일을 초과할 수 없습니다.");
        }
        super.setOutDuration(outDuration);
    }

    /**
     * 출석 횟수 업데이트
     *
     * @param attendCount 출석 횟수
     */
    @Override
    public void setTotalAttendCount(Integer attendCount) {
        if (attendCount > 7) {
            throw new StatsException("출석 횟수는 7일을 초과할 수 없습니다.");
        }
        super.setTotalAttendCount(attendCount);
    }

    /**
     * 주간 통계의 마지막 날짜를 반환합니다.
     *
     * @return 주간 통계의 마지막 날짜 (일요일)
     */
    @Override
    public LocalDate getEndDate() {
        LocalDate startDate = super.getEffectiveDate();
        return DatePolicy.getDateOfWeekDay(startDate, DayOfWeek.SUNDAY);
    }
}