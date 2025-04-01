package com.example.fingerprint_backend.repository;

import com.example.fingerprint_backend.entity.LineEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LineRepository extends JpaRepository<LineEntity, Long> {
    boolean existsByLineId(String lineId);
    Optional<LineEntity> findByLineId(String lineId);
}
