package com.example.fingerprint_backend.repository;

import com.example.fingerprint_backend.entity.LogEntity;
import com.example.fingerprint_backend.types.LogAction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface LogRepository extends JpaRepository<LogEntity, Long> {
    Optional<LogEntity> findByStudentNumberAndActionAndEventTimeAfter(
            String studentNumber,
            LogAction action,
            LocalDateTime eventTime
    );
}
