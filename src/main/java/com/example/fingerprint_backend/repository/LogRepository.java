package com.example.fingerprint_backend.repository;

import com.example.fingerprint_backend.entity.LogEntity;
import com.example.fingerprint_backend.types.LogAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface LogRepository extends JpaRepository<LogEntity, Long> {
    Optional<LogEntity> findByStudentNumberAndActionAndEventTimeAfter(
            String studentNumber,
            LogAction action,
            LocalDateTime eventTime
    );
}
