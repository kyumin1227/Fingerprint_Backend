package com.example.fingerprint_backend.domain.fingerprint.scheduled;

import java.time.LocalDate;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.fingerprint_backend.domain.fingerprint.service.ranking.RankingApplicationService;
import com.example.fingerprint_backend.domain.fingerprint.types.PeriodType;
import com.example.fingerprint_backend.domain.fingerprint.types.RankingType;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RankingScheduled {

    private final RankingApplicationService rankingApplicationService;

    @Scheduled(cron = "0 0 6 * * ?")
    public void recalculateAllRankings() {

        // 일간 체류 시간 랭킹 계산 (어제)
        rankingApplicationService.recalculateRanking(RankingType.체류_시간, PeriodType.일간, LocalDate.now().minusDays(1));

        // 주간 체류 시간 랭킹 계산
        rankingApplicationService.recalculateRanking(RankingType.체류_시간, PeriodType.주간, LocalDate.now());

        // 월간 체류 시간 랭킹 계산
        rankingApplicationService.recalculateRanking(RankingType.체류_시간, PeriodType.월간, LocalDate.now());

        // 주간 등교 시간 랭킹 계산
        rankingApplicationService.recalculateRanking(RankingType.등교_시간, PeriodType.주간, LocalDate.now());

        // 월간 등교 시간 랭킹 계산
        rankingApplicationService.recalculateRanking(RankingType.등교_시간, PeriodType.월간, LocalDate.now());

    }

}
