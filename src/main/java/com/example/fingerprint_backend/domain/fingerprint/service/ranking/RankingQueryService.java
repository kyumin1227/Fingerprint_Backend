package com.example.fingerprint_backend.domain.fingerprint.service.ranking;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.example.fingerprint_backend.domain.fingerprint.util.DatePolicy;
import com.example.fingerprint_backend.domain.fingerprint.util.TimePolicy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.fingerprint_backend.domain.fingerprint.entity.Ranking;
import com.example.fingerprint_backend.domain.fingerprint.repository.RankingRepository;
import com.example.fingerprint_backend.domain.fingerprint.types.PeriodType;
import com.example.fingerprint_backend.domain.fingerprint.types.RankingType;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RankingQueryService {

    private final RankingRepository rankingRepository;

    /**
     * 랭킹 조회
     *
     * @param studentNumber 학생 번호
     * @param rankingType   랭킹 타입
     * @param periodType    기간 타입
     * @param startDate     시작 날짜
     * @return 랭킹 정보
     */
    public Optional<Ranking> getRanking(String studentNumber, RankingType rankingType,
                                        PeriodType periodType, LocalDate startDate) {

        return rankingRepository.findByStudentNumberAndRankingTypeAndPeriodTypeAndStartDate(studentNumber,
                rankingType, periodType, startDate);
    }

    /**
     * 랭킹 리스트 조회 (기간에 따라 날짜 자동 변환)
     *
     * @param rankingType 랭킹 타입
     * @param periodType  기간 타입
     * @param startDate   시작 날짜
     * @return 랭킹 리스트
     */
    public List<Ranking> getRankingList(RankingType rankingType, PeriodType periodType,
                                        LocalDateTime startDate) {

        LocalDate date = TimePolicy.getLocalDate(startDate);

        if (periodType == PeriodType.주간) {
            date = DatePolicy.getDateOfWeekDay(startDate, DayOfWeek.MONDAY);
        } else if (periodType == PeriodType.월간) {
            date = DatePolicy.getMonthStartDate(startDate);
        }

        return rankingRepository.findAllByRankingTypeAndPeriodTypeAndStartDate(rankingType, periodType, date);
    }

    /**
     * 랭킹 리스트 조회 (기간에 따라 날짜 자동 변환)
     *
     * @param rankingType 랭킹 타입
     * @param periodType  기간 타입
     * @param startDate   시작 날짜
     * @return 랭킹 리스트
     */
    public List<Ranking> getRankingList(RankingType rankingType, PeriodType periodType,
                                        LocalDate startDate) {

        if (periodType == PeriodType.주간) {
            startDate = DatePolicy.getDateOfWeekDay(startDate, DayOfWeek.MONDAY);
        } else if (periodType == PeriodType.월간) {
            startDate = DatePolicy.getMonthStartDate(startDate);
        }

        return rankingRepository.findAllByRankingTypeAndPeriodTypeAndStartDate(rankingType, periodType, startDate);
    }

}
