package com.example.fingerprint_backend.domain.fingerprint.repository;

import com.example.fingerprint_backend.domain.fingerprint.entity.MonthlyStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface MonthlyStatsRepository extends JpaRepository<MonthlyStats, Long> {
    Optional<MonthlyStats> findByStudentNumberAndEffectiveDate(String studentNumber, LocalDate effectiveDate);
}
