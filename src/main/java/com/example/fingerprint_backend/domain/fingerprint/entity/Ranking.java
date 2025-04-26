package com.example.fingerprint_backend.domain.fingerprint.entity;

import com.example.fingerprint_backend.domain.fingerprint.exception.RankException;
import com.example.fingerprint_backend.domain.fingerprint.types.PeriodType;
import com.example.fingerprint_backend.domain.fingerprint.types.RankingType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = { "studentNumber", "rankingType", "periodType", "startDate" }) })
public class Ranking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String studentNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RankingType rankingType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PeriodType periodType;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private int rank_order;

    @Builder
    public Ranking(String studentNumber, RankingType rankingType, PeriodType periodType, LocalDate startDate) {
        this.studentNumber = studentNumber;
        this.rankingType = rankingType;
        this.periodType = periodType;
        this.startDate = startDate;
    }

    /**
     * 랭킹 업데이트
     * 
     * @param rank 랭킹
     */
    public void updateRank(int rank) {
        if (rank < 0) {
            throw new RankException("랭킹은 음수일 수 없습니다.");
        }

        this.rank_order = rank;
    }

}
