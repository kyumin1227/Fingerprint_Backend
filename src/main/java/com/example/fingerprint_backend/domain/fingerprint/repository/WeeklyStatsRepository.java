package com.example.fingerprint_backend.domain.fingerprint.repository;

import com.example.fingerprint_backend.domain.fingerprint.entity.WeeklyStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface WeeklyStatsRepository extends JpaRepository<WeeklyStats, Long> {
    Optional<WeeklyStats> findByStudentNumberAndStartDate(String studentNumber, LocalDate startDate);
}
