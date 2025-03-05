package com.example.fingerprint_backend.repository;

import com.example.fingerprint_backend.entity.SchoolClass;
import com.example.fingerprint_backend.entity.CleanArea;
import com.example.fingerprint_backend.entity.CleanSchedule;
import com.example.fingerprint_backend.service.CleanScheduleGroupService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CleanScheduleRepository extends JpaRepository<CleanSchedule, Long> {
    boolean existsByDateAndSchoolClassAndCleanArea(LocalDate date, SchoolClass schoolClass, CleanArea cleanArea);
    boolean existsByDateAndSchoolClassAndCleanAreaAndIsCanceled(LocalDate date, SchoolClass schoolClass, CleanArea cleanArea, boolean isCanceled);
    Optional<CleanSchedule> findByDateAndSchoolClassAndCleanArea(LocalDate date, SchoolClass schoolClass, CleanArea cleanArea);
    Optional<CleanSchedule> findTopBySchoolClassAndCleanAreaAndIsCanceledOrderByDateDesc(SchoolClass schoolClass, CleanArea cleanArea, boolean canceled);
    List<CleanSchedule> findAllByDateAfterAndSchoolClass(LocalDate date, SchoolClass schoolClass);
//  특정 날짜 이후, 구역에 해당하는 청소 스케줄을 가져오는 메소드
    List<CleanSchedule> findAllByDateGreaterThanEqualAndCleanArea(LocalDate dateAfter, CleanArea cleanArea);
    List<CleanSchedule> findAllByDateAndIsCanceledAndIsCompleted(LocalDate date, boolean isCanceled, boolean isCompleted);
}
