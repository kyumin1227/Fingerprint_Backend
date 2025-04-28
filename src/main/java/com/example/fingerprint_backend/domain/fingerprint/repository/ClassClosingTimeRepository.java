package com.example.fingerprint_backend.domain.fingerprint.repository;

import com.example.fingerprint_backend.domain.fingerprint.entity.ClassClosingTime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ClassClosingTimeRepository extends JpaRepository<ClassClosingTime, Long> {
    Optional<ClassClosingTime> findTopBySchoolClassIdAndClosingTimeBetweenOrderByClosingTimeAsc(Long schoolClassId, LocalDateTime startTime, LocalDateTime endTime);
}
