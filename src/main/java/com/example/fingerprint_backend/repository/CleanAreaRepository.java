package com.example.fingerprint_backend.repository;

import com.example.fingerprint_backend.entity.CleanArea;
import com.example.fingerprint_backend.entity.SchoolClass;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CleanAreaRepository extends JpaRepository<CleanArea, Long> {
    boolean existsByNameAndSchoolClass(String name, SchoolClass schoolClass);

    Optional<CleanArea> findByNameAndSchoolClass(String areaName, SchoolClass schoolClass);
}
