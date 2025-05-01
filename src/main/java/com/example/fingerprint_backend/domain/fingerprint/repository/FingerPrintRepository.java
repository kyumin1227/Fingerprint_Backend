package com.example.fingerprint_backend.domain.fingerprint.repository;

import com.example.fingerprint_backend.domain.fingerprint.entity.FingerPrintEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FingerPrintRepository extends JpaRepository<FingerPrintEntity, String> {
}
