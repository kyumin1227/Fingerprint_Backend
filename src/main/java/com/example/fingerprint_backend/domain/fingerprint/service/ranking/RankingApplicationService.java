package com.example.fingerprint_backend.domain.fingerprint.service.ranking;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.example.fingerprint_backend.domain.fingerprint.entity.BaseStats;
import com.example.fingerprint_backend.domain.fingerprint.service.stats.StatsApplicationService;
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
    private final StatsApplicationService statsApplicationService;

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
                                         LocalDate startDate,
                                         int rank) {

        Ranking ranking = rankingCommandService.getOrCreateRanking(studentNumber, rankingType, periodType, startDate);

        return rankingCommandService.updateRanking(ranking, rank);
    }

    /**
     * 정렬된 통계 리스트를 기반으로 랭킹 업데이트
     *
     * @param sortedList  정렬된 통계 리스트
     * @param rankingType 랭킹 타입
     * @param periodType  기간 타입
     * @param startDate   시작 날짜
     */
    public void updateRankings(List<? extends BaseStats> sortedList, RankingType rankingType, PeriodType periodType,
                               LocalDate startDate) {
        for (int i = 0; i < sortedList.size(); i++) {
            BaseStats stats = sortedList.get(i);
            String studentNumber = stats.getStudentNumber();
            int rank = i + 1;

            // 랭킹 생성 또는 업데이트
            createOrUpdateRanking(studentNumber, rankingType, periodType, startDate, rank);
        }
    }

    /**
     * 랭킹 재계산
     *
     * @param rankingType 랭킹 타입
     * @param periodType  기간 타입
     * @param startDate   시작 날짜
     */
    public void recalculateRanking(RankingType rankingType, PeriodType periodType, LocalDate startDate) {

        List<? extends BaseStats> orderBy = new ArrayList<>();

        if (rankingType == RankingType.체류_시간) {
             orderBy = statsApplicationService.getStatsOrderedByStayDuration(periodType, startDate);
        } else if (rankingType == RankingType.등교_시간) {
            orderBy = statsApplicationService.getStatsOrderedByAttendanceTime(periodType, startDate);
        }

        updateRankings(orderBy, rankingType, periodType, startDate);

    }

}
