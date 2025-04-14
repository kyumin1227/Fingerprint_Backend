package com.example.fingerprint_backend.repository;

import com.example.fingerprint_backend.entity.SchoolClass;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SchoolClassRepository extends JpaRepository<SchoolClass, Long> {
    boolean existsSchoolClassByName(String className);
    Optional<SchoolClass> findSchoolClassByName(String className);
}
