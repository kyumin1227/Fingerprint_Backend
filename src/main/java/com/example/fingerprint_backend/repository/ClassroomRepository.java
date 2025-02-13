package com.example.fingerprint_backend.repository;

import com.example.fingerprint_backend.entity.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClassroomRepository extends JpaRepository<Classroom, Long> {
    Optional<Classroom> findByName(String className);
    boolean existsClassroomByName(String className);
}
