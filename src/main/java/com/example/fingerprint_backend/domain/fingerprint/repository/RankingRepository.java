package com.example.fingerprint_backend.domain.fingerprint.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fingerprint_backend.domain.fingerprint.entity.Ranking;

public interface RankingRepository extends JpaRepository<Ranking, Long> {

}
