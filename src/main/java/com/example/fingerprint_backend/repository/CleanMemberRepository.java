package com.example.fingerprint_backend.repository;

import com.example.fingerprint_backend.entity.CleanMember;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CleanMemberRepository extends JpaRepository<CleanMember, String> {
    boolean existsByStudentNumber(String studentNumber);
    boolean existsByStudentNumberAndSchoolClassId(String studentNumber, Long schoolClassId);
    Optional<CleanMember> findByStudentNumber(String studentNumber);
}
