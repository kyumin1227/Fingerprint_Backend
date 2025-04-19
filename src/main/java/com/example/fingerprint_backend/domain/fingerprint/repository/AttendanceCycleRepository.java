package com.example.fingerprint_backend.domain.fingerprint.repository;

import com.example.fingerprint_backend.domain.fingerprint.entity.AttendanceCycle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceCycleRepository extends JpaRepository<AttendanceCycle, Long> {
}
