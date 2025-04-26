package com.example.fingerprint_backend.domain.fingerprint.service.ranking;

import java.util.Optional;

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
                                        PeriodType periodType, String startDate) {

        return rankingRepository.findByStudentNumberAndRankingTypeAndPeriodTypeAndStartDate(studentNumber,
                rankingType, periodType, startDate);
    }

}
