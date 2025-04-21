package com.example.fingerprint_backend.domain.fingerprint.entity;

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
public class DailyStats extends BaseStats {
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
        this.studentNumber = studentNumber;
        this.effectiveDate = effectiveDate;
        this.dayOfWeek = effectiveDate.getDayOfWeek();
    }
}
