package com.example.fingerprint_backend.repository;

import com.example.fingerprint_backend.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
}
