package com.example.fingerprint_backend.domain.fingerprint.repository;

import com.example.fingerprint_backend.domain.fingerprint.entity.MonthlyStats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonthlyStatsRepository extends JpaRepository<MonthlyStats, Long> {
}
