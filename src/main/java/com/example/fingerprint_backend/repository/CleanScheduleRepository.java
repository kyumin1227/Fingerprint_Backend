package com.example.fingerprint_backend.repository;

import com.example.fingerprint_backend.entity.CleanSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CleanScheduleRepository extends JpaRepository<CleanSchedule, Long> {
}
