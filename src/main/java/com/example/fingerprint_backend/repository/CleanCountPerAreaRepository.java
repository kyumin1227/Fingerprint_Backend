package com.example.fingerprint_backend.repository;

import com.example.fingerprint_backend.entity.CleanCountPerArea;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CleanCountPerAreaRepository extends JpaRepository<CleanCountPerArea, Long> {
    Optional<CleanCountPerArea> findCleanCountPerAreaByStudentNumberAndCleanAreaId(String studentNumber, Long cleanAreaId);
}
