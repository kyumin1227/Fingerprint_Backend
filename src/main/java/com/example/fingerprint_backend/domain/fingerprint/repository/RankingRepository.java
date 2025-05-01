package com.example.fingerprint_backend.domain.fingerprint.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fingerprint_backend.domain.fingerprint.entity.Ranking;
import com.example.fingerprint_backend.domain.fingerprint.types.PeriodType;
import com.example.fingerprint_backend.domain.fingerprint.types.RankingType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RankingRepository extends JpaRepository<Ranking, Long> {

    Optional<Ranking> findByStudentNumberAndRankingTypeAndPeriodTypeAndStartDate(String studentNumber,
                                                                                 RankingType rankingType, PeriodType periodType, LocalDate startDate);

    List<Ranking> findAllByRankingTypeAndPeriodTypeAndStartDate(RankingType rankingType, PeriodType periodType, LocalDate startDate);

}
