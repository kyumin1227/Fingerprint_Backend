package com.example.fingerprint_backend.repository;

import com.example.fingerprint_backend.entity.CleanGroup;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CleanGroupRepository extends JpaRepository<CleanGroup, Long> {
    boolean existsById(@NotNull Long id);
}
