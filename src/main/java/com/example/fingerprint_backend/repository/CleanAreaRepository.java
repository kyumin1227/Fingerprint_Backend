package com.example.fingerprint_backend.repository;

import com.example.fingerprint_backend.entity.CleanArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CleanAreaRepository extends JpaRepository<CleanArea, Long> {
}
