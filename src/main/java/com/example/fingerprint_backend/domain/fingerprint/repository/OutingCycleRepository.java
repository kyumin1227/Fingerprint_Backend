package com.example.fingerprint_backend.domain.fingerprint.repository;

import com.example.fingerprint_backend.domain.fingerprint.entity.OutingCycle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutingCycleRepository extends JpaRepository<OutingCycle, Long> {
}
