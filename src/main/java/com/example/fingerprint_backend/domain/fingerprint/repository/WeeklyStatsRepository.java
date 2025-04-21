package com.example.fingerprint_backend.domain.fingerprint.repository;

import com.example.fingerprint_backend.domain.fingerprint.entity.WeeklyStats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeeklyStatsRepository extends JpaRepository<WeeklyStats, Long> {
}
