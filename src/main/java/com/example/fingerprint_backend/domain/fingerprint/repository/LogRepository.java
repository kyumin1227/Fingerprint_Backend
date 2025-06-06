package com.example.fingerprint_backend.domain.fingerprint.repository;

import com.example.fingerprint_backend.domain.fingerprint.entity.LogEntity;
import com.example.fingerprint_backend.domain.fingerprint.types.LogAction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LogRepository extends JpaRepository<LogEntity, Long> {
    Optional<LogEntity> findByStudentNumberAndActionAndEventTimeAfter(
            String studentNumber,
            LogAction action,
            LocalDateTime eventTime
    );

    List<LogEntity> findByStudentNumberAndActionAndEventTimeBetween(
            String studentNumber,
            LogAction action,
            LocalDateTime startTime,
            LocalDateTime endTime
    );

    List<LogEntity> findByActionAndEventTimeBetween(
            LogAction action,
            LocalDateTime eventTimeAfter,
            LocalDateTime eventTimeBefore
    );

    List<LogEntity> findByEventTimeBetween(
            LocalDateTime eventTimeAfter,
            LocalDateTime eventTimeBefore
    );
}
