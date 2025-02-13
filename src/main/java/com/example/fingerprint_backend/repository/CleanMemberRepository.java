package com.example.fingerprint_backend.repository;

import com.example.fingerprint_backend.entity.CleanMember;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CleanMemberRepository extends JpaRepository<CleanMember, String> {
    boolean existsCleanMemberByStudentNumber(String studentNumber);
    Optional<CleanMember> getCleanMemberByStudentNumber(String studentNumber);
}
