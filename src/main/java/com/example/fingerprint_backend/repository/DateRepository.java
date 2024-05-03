package com.example.fingerprint_backend.repository;

import com.example.fingerprint_backend.entity.DateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface DateRepository extends JpaRepository<DateEntity, Integer> {

    Optional<DateEntity> findByDate(LocalDate date);
}
