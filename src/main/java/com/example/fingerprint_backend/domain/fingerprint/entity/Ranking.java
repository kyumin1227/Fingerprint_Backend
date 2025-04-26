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
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ranking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String studentNumber;

    @Column(nullable = false)
    private int rank;

    @Column(nullable = false)
    private String startDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RankingType rankingType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PeriodType periodType;

    @Builder
    public Ranking(String studentNumber, int rank, String startDate, RankingType rankingType, PeriodType periodType) {
        if (rank < 0) {
            throw new RankException("랭킹은 음수일 수 없습니다.");
        }
        this.studentNumber = studentNumber;
        this.rank = rank;
        this.startDate = startDate;
        this.rankingType = rankingType;
        this.periodType = periodType;
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

        this.rank = rank;
    }

}
