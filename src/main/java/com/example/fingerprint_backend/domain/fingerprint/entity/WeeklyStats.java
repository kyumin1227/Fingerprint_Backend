package com.example.fingerprint_backend.domain.fingerprint.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "weekly_stats", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"student_number", "start_date"})
})
public class WeeklyStats extends BaseStats {
    @Builder
    public WeeklyStats(String studentNumber, LocalDate startDate) {
        super(studentNumber, startDate);
    }
}