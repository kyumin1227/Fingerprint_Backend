package com.example.fingerprint_backend.domain.fingerprint.repository;

import com.example.fingerprint_backend.domain.fingerprint.entity.OutingCycle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OutingCycleRepository extends JpaRepository<OutingCycle, Long> {
    Optional<OutingCycle> findTopByStudentNumberAndOutingEndTimeIsNullOrderByOutingStartTimeDesc(String studentNumber);
    Optional<OutingCycle> findTopByStudentNumberOrderByOutingStartTimeDesc(String studentNumber);
}
