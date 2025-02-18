package com.example.fingerprint_backend.repository;

import com.example.fingerprint_backend.entity.CleanArea;
import com.example.fingerprint_backend.entity.CleanGroup;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CleanGroupRepository extends JpaRepository<CleanGroup, Long> {
    boolean existsById(@NotNull Long id);
    List<CleanGroup> findByIsCleanedAndCleanArea(boolean isCleaned, CleanArea cleanArea);
    Optional<CleanGroup> findTopByCleanAreaAndIsCleanedOrderByIdDesc(CleanArea cleanArea, boolean isCleaned);
}
