package com.example.fingerprint_backend.domain.fingerprint.entity;

import com.example.fingerprint_backend.domain.fingerprint.exception.StatsException;
import jakarta.persistence.Column;
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
public class MonthlyStats extends BaseStats {

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
    public long getMaxDuration() {
        return YearMonth.of(getStartDate().getYear(), getStartDate().getMonthValue()).lengthOfMonth()
                * 24 * 60 * 60 * 1000L;
    }

    /**
     * 체류 시간 업데이트
     *
     * @param stayDuration 추가할 체류 시간 (밀리초 단위)
     */
    public void updateTotalStayDuration(Long stayDuration) {
        if (super.getTotalStayDuration() + stayDuration > getMaxDuration()) {
            throw new StatsException("체류 시간은 해당 월의 최대 시간을 초과할 수 없습니다.");
        }
        super.updateTotalStayDuration(stayDuration);
    }

    /**
     * 외출 시간 업데이트
     *
     * @param outDuration 추가할 외출 시간 (밀리초 단위)
     */
    public void updateTotalOutDuration(Long outDuration) {
        if (super.getTotalOutDuration() + outDuration > getMaxDuration()) {
            throw new StatsException("외출 시간은 해당 월의 최대 시간을 초과할 수 없습니다.");
        }
        super.updateTotalOutDuration(outDuration);
    }
}
