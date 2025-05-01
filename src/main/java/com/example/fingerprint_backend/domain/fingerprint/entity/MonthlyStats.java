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

import java.time.LocalDate;
import java.time.YearMonth;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "monthly_stats", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"student_number", "start_date"})
})
public class MonthlyStats extends ContinuousStats {

    @Builder
    public MonthlyStats(String studentNumber, LocalDate startDate) {
        super(studentNumber, startDate);
        if (startDate.getDayOfMonth() != 1) {
            throw new StatsException("월간 통계는 해당 월의 첫 날부터 시작해야 합니다.");
        }
    }

    /**
     * 해당 월의 최대 시간 (밀리초 단위)
     *
     * @return 해당 월의 최대 시간 (밀리초 단위)
     */
    public Integer getMaxDays() {
        return YearMonth.of(getEffectiveDate().getYear(), getEffectiveDate().getMonthValue()).lengthOfMonth();
    }

    /**
     * 총 체류 시간 재설정
     *
     * @param stayDuration 총 체류 시간 (밀리초 단위)
     */
    @Override
    public void setStayDuration(Long stayDuration) {
        if (stayDuration > getMaxDays() * 24 * 60 * 60 * 1000L) {
            throw new StatsException("체류 시간은 해당 월의 최대 시간을 초과할 수 없습니다.");
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
        if (outDuration > getMaxDays() * 24 * 60 * 60 * 1000L) {
            throw new StatsException("외출 시간은 해당 월의 최대 시간을 초과할 수 없습니다.");
        }
        super.setOutDuration(outDuration);
    }

    @Override
    public void setTotalAttendCount(Integer totalAttendCount) {
        if (totalAttendCount > getMaxDays()) {
            throw new StatsException("출석 횟수는 해당 월의 최대 시간을 초과할 수 없습니다.");
        }
        super.setTotalAttendCount(totalAttendCount);
    }

    /**
     * 월간 통계의 마지막 날짜를 반환합니다.
     *
     * @return 월간 통계의 마지막 날짜 (해당 월의 마지막 날)
     */
    @Override
    public LocalDate getEndDate() {
        LocalDate startDate = super.getEffectiveDate();
        return DatePolicy.getMonthEndDate(startDate);
    }
}
