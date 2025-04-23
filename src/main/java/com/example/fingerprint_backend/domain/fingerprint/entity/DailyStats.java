package com.example.fingerprint_backend.domain.fingerprint.entity;

import com.example.fingerprint_backend.domain.fingerprint.exception.StatsException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "daily_stats", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "student_number", "effective_date" })
})
public class DailyStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String studentNumber;

    @Column(nullable = false)
    private LocalDate effectiveDate;

    @Column(nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(nullable = false)
    private Long stayDuration = 0L;

    @Column(nullable = false)
    private Long outDuration = 0L;

    @Builder
    public DailyStats(String studentNumber, LocalDate effectiveDate) {
        if (studentNumber == null || studentNumber.isEmpty()) {
            throw new StatsException("학생 번호는 비어있을 수 없습니다.");
        }
        if (effectiveDate == null) {
            throw new StatsException("유효한 날짜가 아닙니다.");
        }
        this.studentNumber = studentNumber;
        this.effectiveDate = effectiveDate;
        this.dayOfWeek = effectiveDate.getDayOfWeek();
    }

    private static final Long MAX_DURATION = 24 * 60 * 60 * 1000L; // 24시간

    /**
     * 체류 시간 업데이트
     *
     * @param stayDuration 추가할 체류 시간 (밀리초 단위)
     */
    public void updateStayDuration(Long stayDuration) {
        if (stayDuration < 0) {
            throw new StatsException("체류 시간은 음수를 더할 수 없습니다.");
        }
        if (this.stayDuration + stayDuration > MAX_DURATION) {
            throw new StatsException("체류 시간은 24시간을 초과할 수 없습니다.");
        }
        this.stayDuration += stayDuration;
    }

    /**
     * 외출 시간 업데이트
     *
     * @param outDuration 추가할 외출 시간 (밀리초 단위)
     */
    public void updateOutDuration(Long outDuration) {
        if (outDuration < 0) {
            throw new StatsException("외출 시간은 음수를 더할 수 없습니다.");
        }
        if (this.outDuration + outDuration > MAX_DURATION) {
            throw new StatsException("외출 시간은 24시간을 초과할 수 없습니다.");
        }
        this.outDuration += outDuration;
    }
}
