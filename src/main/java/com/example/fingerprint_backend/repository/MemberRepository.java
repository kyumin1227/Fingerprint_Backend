package com.example.fingerprint_backend.repository;

import com.example.fingerprint_backend.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    Optional<MemberEntity> findByEmail(String email);
    Optional<MemberEntity> findByStudentNumber(String studentNumber);
    Optional<MemberEntity> findByKakao(String kakao);
    MemberEntity getByStudentNumber(String studentNumber);
}
