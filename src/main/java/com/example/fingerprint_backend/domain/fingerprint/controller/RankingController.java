package com.example.fingerprint_backend.domain.fingerprint.controller;

import com.example.fingerprint_backend.ApiResult;
import com.example.fingerprint_backend.domain.fingerprint.dto.RankingResponseDto;
import com.example.fingerprint_backend.domain.fingerprint.service.ranking.RankingApplicationService;
import com.example.fingerprint_backend.domain.fingerprint.types.PeriodType;
import com.example.fingerprint_backend.domain.fingerprint.types.RankingType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Tag(name = "Fingerprint - ranking", description = "랭킹 API / ランキングAPI")
@RestController
@RequestMapping("/api/ranking")
@RequiredArgsConstructor
public class RankingController {

        private final RankingApplicationService rankingApplicationService;

        @Operation(summary = "랭킹 조회 / ランキング", description = "랭킹 타입, 기간타입, 시작일를 이용한 랭킹을 조회합니다. / ランキングタイプ、期間タイプ、開始日を使用してランキングを取得します。")
        @GetMapping("")
        public ResponseEntity<ApiResult> getRanking(
                        @Parameter(description = "랭킹 타입 (DAILY: 일간, WEEKLY: 주간, MONTHLY: 월간)", required = true, schema = @Schema(implementation = RankingType.class)) @RequestParam RankingType rankingType,

                        @Parameter(description = "기간 타입 (DAY: 일, WEEK: 주, MONTH: 월)", required = true, schema = @Schema(implementation = PeriodType.class)) @RequestParam PeriodType periodType,

                        @Parameter(description = "시작일 (YYYY-MM-DD 형식)", required = true, example = "2024-03-01") @RequestParam LocalDate startDate) {
                Integer RANKING_LIMIT = 5;

                RankingResponseDto ranking = rankingApplicationService.getRankingResponseDto(
                                rankingType,
                                periodType,
                                startDate,
                                RANKING_LIMIT);

                return ResponseEntity.status(HttpStatus.OK).body(
                                new ApiResult(true, "랭킹 조회 성공", ranking));
        }
}
