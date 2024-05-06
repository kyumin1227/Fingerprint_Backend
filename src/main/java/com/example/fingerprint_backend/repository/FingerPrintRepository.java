package com.example.fingerprint_backend.repository;

import com.example.fingerprint_backend.entity.FingerPrintEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FingerPrintRepository extends JpaRepository<FingerPrintEntity, String> {
}
