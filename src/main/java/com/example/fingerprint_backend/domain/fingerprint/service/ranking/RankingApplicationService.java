package com.example.fingerprint_backend.domain.fingerprint.service.ranking;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import com.example.fingerprint_backend.domain.fingerprint.entity.BaseStats;
import com.example.fingerprint_backend.domain.fingerprint.entity.ContinuousStats;
import com.example.fingerprint_backend.domain.fingerprint.service.stats.StatsApplicationService;
import com.example.fingerprint_backend.domain.fingerprint.util.RankingCalculator;
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
                                         String startDate,
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
                               String startDate) {
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

        // 랭킹 계산할 통계 리스트 가져오기
        List<? extends BaseStats> statsList = statsApplicationService.getStatsListByPeriodTypeAndDate(periodType, startDate);

        if (rankingType.equals(RankingType.등교_시간)) {

            List<ContinuousStats> continuousStats = RankingCalculator.convertListType(statsList, ContinuousStats.class);

            List<ContinuousStats> sortedList = continuousStats.stream()
                    .sorted(Comparator
                            .comparing(BaseStats::getStayDuration))
                    .toList();

            updateRankings(sortedList, rankingType, periodType, startDate.toString());

        } else if (rankingType.equals(RankingType.체류_시간)) {

            List<BaseStats> baseStats = RankingCalculator.convertListType(statsList, BaseStats.class);

            List<BaseStats> sortedList = baseStats.stream()
                    .sorted(Comparator
                            .comparing(BaseStats::getStayDuration))
                    .toList();

            updateRankings(sortedList, rankingType, periodType, startDate.toString());

        } else {
            throw new IllegalArgumentException("Invalid ranking type: " + rankingType);
        }

    }

}
