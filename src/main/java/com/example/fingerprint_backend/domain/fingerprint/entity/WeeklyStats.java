package com.example.fingerprint_backend.domain.fingerprint.entity;

import com.example.fingerprint_backend.domain.fingerprint.exception.StatsException;
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
public class WeeklyStats extends BaseStats {

    private static final Long MAX_DURATION = 7 * 24 * 60 * 60 * 1000L; // 24시간 * 7일

    @Builder
    public WeeklyStats(String studentNumber, LocalDate startDate) {
        super(studentNumber, startDate);
        if (!startDate.getDayOfWeek().equals(DayOfWeek.MONDAY)) {
            throw new StatsException("주간 통계는 월요일부터 시작해야 합니다.");
        }
    }

    /**
     * 체류 시간 업데이트
     *
     * @param stayDuration 추가할 체류 시간 (밀리초 단위)
     */
    public void updateTotalStayDuration(Long stayDuration) {
        if (super.getTotalStayDuration() + stayDuration > MAX_DURATION) {
            throw new StatsException("체류 시간은 7일을 초과할 수 없습니다.");
        }
        super.updateTotalStayDuration(stayDuration);
    }

    /**
     * 외출 시간 업데이트
     *
     * @param outDuration 추가할 외출 시간 (밀리초 단위)
     */
    public void updateTotalOutDuration(Long outDuration) {
        if (super.getTotalStayDuration() + outDuration > MAX_DURATION) {
            throw new StatsException("외출 시간은 7일을 초과할 수 없습니다.");
        }
        super.updateTotalStayDuration(outDuration);
    }
}