package com.example.fingerprint_backend.repository;

import com.example.fingerprint_backend.entity.CleanRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CleanRecordRepository extends JpaRepository<CleanRecord, Long> {

}
