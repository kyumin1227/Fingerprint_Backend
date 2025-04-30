package com.example.fingerprint_backend.domain.fingerprint.service.ranking;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.fingerprint_backend.domain.fingerprint.dto.RankEntityDto;
import com.example.fingerprint_backend.domain.fingerprint.dto.RankingResponseDto;
import com.example.fingerprint_backend.domain.fingerprint.entity.BaseStats;
import com.example.fingerprint_backend.domain.fingerprint.entity.ContinuousStats;
import com.example.fingerprint_backend.domain.fingerprint.service.stats.StatsApplicationService;
import com.example.fingerprint_backend.domain.fingerprint.util.DatePolicy;
import com.example.fingerprint_backend.domain.fingerprint.util.TimePolicy;
import com.example.fingerprint_backend.entity.MemberEntity;
import com.example.fingerprint_backend.service.Member.MemberQueryService;
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
    private final MemberQueryService memberQueryService;

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

    /**
     * 일일 출석 랭킹 생성
     *
     * @param studentNumber  학생 번호
     * @param attendanceTime 시작 날짜
     */
    public Ranking createDailyAttendanceRanking(String studentNumber, LocalDateTime attendanceTime) {

        Optional<Ranking> prevRanking = rankingQueryService.getRanking(studentNumber, RankingType.등교_시간, PeriodType.일간, TimePolicy.getLocalDate(attendanceTime));
        if (prevRanking.isPresent()) {
            return prevRanking.get();
        }

        List<Ranking> rankingList = rankingQueryService.getRankingList(RankingType.등교_시간, PeriodType.일간, attendanceTime);
        Ranking ranking = rankingCommandService.createRanking(studentNumber, RankingType.등교_시간, PeriodType.일간, TimePolicy.getLocalDate(attendanceTime));
        ranking.updateRank(rankingList.size() + 1);
        return ranking;
    }

    public RankingResponseDto getRankingResponseDto(RankingType rankingType, PeriodType periodType, LocalDate startDate, Integer limit) {

        // TODO 추후 redis 적용 및 로직 수정 예정

        List<? extends BaseStats> orderBy = new ArrayList<>();

        if (rankingType == RankingType.체류_시간) {
            orderBy = statsApplicationService.getStatsOrderedByStayDuration(periodType, startDate);
        } else if (rankingType == RankingType.등교_시간) {
            orderBy = statsApplicationService.getStatsOrderedByAttendanceTime(periodType, startDate);
        }

        orderBy = orderBy.subList(0, Math.min(orderBy.size(), limit));

        List<RankEntityDto> rankList = new ArrayList<>();

        if (rankingType == RankingType.체류_시간) {
            for (int i = 0; i < orderBy.size(); i++) {
                BaseStats stats = orderBy.get(i);
                String studentNumber = stats.getStudentNumber();
                MemberEntity member = memberQueryService.getMemberByStudentNumber(studentNumber);
                int rank = i + 1;

                RankEntityDto rankEntityDto = new RankEntityDto(
                        studentNumber,
                        member.getGivenName(),
                        member.getFamilyName(),
                        member.getProfileImage(),
                        rank,
                        stats.getStayDuration(),
                        0
                );

                rankList.add(rankEntityDto);

            }
        } else if (rankingType == RankingType.등교_시간) {
            for (int i = 0; i < orderBy.size(); i++) {
                ContinuousStats stats = (ContinuousStats) orderBy.get(i);
                String studentNumber = stats.getStudentNumber();
                MemberEntity member = memberQueryService.getMemberByStudentNumber(studentNumber);
                int rank = i + 1;

                RankEntityDto rankEntityDto = new RankEntityDto(
                        studentNumber,
                        member.getGivenName(),
                        member.getFamilyName(),
                        member.getProfileImage(),
                        rank,
                        stats.getAvgAttendTime(),
                        stats.getTotalAttendCount()
                );

                rankList.add(rankEntityDto);

            }
        }

        return new RankingResponseDto(
                rankingType,
                periodType,
                DatePolicy.getDateByPeriodType(startDate, periodType),
                rankList
        );
    }

}
