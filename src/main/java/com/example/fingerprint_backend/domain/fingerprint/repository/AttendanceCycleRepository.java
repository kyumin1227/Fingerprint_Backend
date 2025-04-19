package com.example.fingerprint_backend.domain.fingerprint.repository;

import com.example.fingerprint_backend.domain.fingerprint.entity.AttendanceCycle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AttendanceCycleRepository extends JpaRepository<AttendanceCycle, Long> {
    Optional<AttendanceCycle> findTopByStudentNumberAndLeaveTimeIsNullOrderByAttendTimeDesc(String studentNumber);
    Optional<AttendanceCycle> findTopByStudentNumberOrderByAttendTimeDesc(String studentNumber);
}
