package com.example.fingerprint_backend.domain.fingerprint.repository;

import com.example.fingerprint_backend.domain.fingerprint.entity.DailyStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyStatsRepository extends JpaRepository<DailyStats, Long> {

    Optional<DailyStats> findByStudentNumber(String studentNumber);

    Optional<DailyStats> findByStudentNumberAndEffectiveDate(String studentNumber, LocalDate date);

    List<DailyStats> findByStudentNumberAndEffectiveDateBetween(String studentNumber, LocalDate startDate, LocalDate endDate);

    List<DailyStats> findAllByEffectiveDate(LocalDate date);

    List<DailyStats> findAllByEffectiveDateOrderByStayDurationDesc(LocalDate date);
}
