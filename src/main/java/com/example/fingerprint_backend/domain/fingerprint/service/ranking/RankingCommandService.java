package com.example.fingerprint_backend.domain.fingerprint.service.ranking;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.fingerprint_backend.domain.fingerprint.entity.Ranking;
import com.example.fingerprint_backend.domain.fingerprint.repository.RankingRepository;
import com.example.fingerprint_backend.domain.fingerprint.types.PeriodType;
import com.example.fingerprint_backend.domain.fingerprint.types.RankingType;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Service
@Transactional
@RequiredArgsConstructor
public class RankingCommandService {

    private final RankingRepository rankingRepository;
    private final RankingQueryService rankingQueryService;

    /**
     * 랭킹 생성
     *
     * @param studentNumber 학번
     * @param rankingType   랭킹 타입
     * @param periodType    기간 타입
     * @param startDate     시작일
     * @return Ranking
     */
    public Ranking createRanking(String studentNumber, RankingType rankingType,
                                 PeriodType periodType, LocalDate startDate) {

        Ranking ranking = Ranking.builder()
                .studentNumber(studentNumber)
                .rankingType(rankingType)
                .periodType(periodType)
                .startDate(startDate)
                .build();

        return rankingRepository.save(ranking);
    }

    /**
     * 랭킹 조회 또는 생성
     *
     * @param studentNumber 학번
     * @param rankingType   랭킹 타입
     * @param periodType    기간 타입
     * @param startDate     시작일
     * @return Ranking
     */
    public Ranking getOrCreateRanking(String studentNumber, RankingType rankingType,
                                      PeriodType periodType, LocalDate startDate) {

        return rankingQueryService.getRanking(studentNumber, rankingType, periodType, startDate)
                .orElseGet(() -> createRanking(studentNumber, rankingType, periodType, startDate));
    }

    /**
     * 랭킹 업데이트
     *
     * @param ranking 랭킹
     * @return Ranking
     */
    public Ranking updateRanking(Ranking ranking, int rank) {

        ranking.updateRank(rank);

        return ranking;
    }

}
