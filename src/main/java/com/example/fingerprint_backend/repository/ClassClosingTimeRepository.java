package com.example.fingerprint_backend.repository;

import com.example.fingerprint_backend.entity.ClassClosingTime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ClassClosingTimeRepository extends JpaRepository<ClassClosingTime, Long> {
    Optional<ClassClosingTime> findBySchoolClassIdAndClosingTimeAfter(Long schoolClassId, LocalDateTime closingTime);
}
