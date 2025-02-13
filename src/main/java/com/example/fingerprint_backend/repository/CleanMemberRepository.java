package com.example.fingerprint_backend.repository;

import com.example.fingerprint_backend.entity.CleanMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CleanMemberRepository extends JpaRepository<CleanMember, String> {
    boolean existsCleanMemberByStudentNumber(String studentNumber);
}
