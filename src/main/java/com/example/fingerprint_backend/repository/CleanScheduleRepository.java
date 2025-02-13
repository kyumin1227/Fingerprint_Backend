package com.example.fingerprint_backend.repository;

import com.example.fingerprint_backend.entity.Classroom;
import com.example.fingerprint_backend.entity.CleanArea;
import com.example.fingerprint_backend.entity.CleanSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface CleanScheduleRepository extends JpaRepository<CleanSchedule, Long> {
    Optional<CleanSchedule> getCleanScheduleByDateAndCleanAreaAndClassroom(LocalDate date, CleanArea cleanArea, Classroom classroom);
}
