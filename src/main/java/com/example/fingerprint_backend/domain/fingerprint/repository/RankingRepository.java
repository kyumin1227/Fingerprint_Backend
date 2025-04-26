package com.example.fingerprint_backend.domain.fingerprint.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fingerprint_backend.domain.fingerprint.entity.Ranking;
import com.example.fingerprint_backend.domain.fingerprint.types.PeriodType;
import com.example.fingerprint_backend.domain.fingerprint.types.RankingType;

import java.util.Optional;

public interface RankingRepository extends JpaRepository<Ranking, Long> {

    Optional<Ranking> findByStudentNumberAndRankingTypeAndPeriodTypeAndStartDate(String studentNumber,
                                                                                 RankingType rankingType, PeriodType periodType, String startDate);

}
