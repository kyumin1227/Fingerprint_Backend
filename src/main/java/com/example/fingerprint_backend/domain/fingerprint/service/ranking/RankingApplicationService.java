package com.example.fingerprint_backend.domain.fingerprint.service.ranking;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.fingerprint_backend.domain.fingerprint.entity.Ranking;
import com.example.fingerprint_backend.domain.fingerprint.types.PeriodType;
import com.example.fingerprint_backend.domain.fingerprint.types.RankingType;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class RankingApplicationService {

    private final RankingCommandService rankingCommandService;
    private final RankingQueryService rankingQueryService;

    /**
     * 랭킹 생성 또는 업데이트
     * 
     * @param studentNumber 학생 번호
     * @param rankingType   랭킹 타입
     * @param periodType    기간 타입
     * @param startDate     시작 날짜
     * @param rank          랭킹
     * @return Ranking
     */
    public Ranking createOrUpdateRanking(String studentNumber, RankingType rankingType, PeriodType periodType,
            String startDate,
            int rank) {

        Ranking ranking = rankingCommandService.getOrCreateRanking(studentNumber, rankingType, periodType, startDate);

        return rankingCommandService.updateRanking(ranking, rank);
    }

    /**
     * 랭킹 재계산
     * 
     * @param rankingType 랭킹 타입
     * @param periodType  기간 타입
     * @param startDate   시작 날짜
     */
    public void recalculateRanking(RankingType rankingType, PeriodType periodType, LocalDate startDate) {

        // 통계 데이터 조회

        // 랭킹 재계산

        // 랭킹 반영

    }

}
