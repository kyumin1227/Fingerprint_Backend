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

    @Operation(operationId = "getRanking",
            summary = "랭킹 조회 / ランキング取得",
            description = "랭킹 타입, 기간타입, 시작일를 이용한 랭킹을 조회합니다.<br>" +
                    "ランキングタイプ、期間タイプ、開始日を使用してランキングを取得します。<br><br>" +
                    "<a href=\"https://documenter.getpostman.com/view/27801312/2sB2cYbKwH\" target=\"_blank\">Response Sample</a>")
    @GetMapping("")
    public ResponseEntity<ApiResult> getRanking(
            @Parameter(description = "랭킹 타입 / ランキングタイプ<br><br>" +
                    "• `등교_시간` - 登校時間<br>" +
                    "• `체류_시간` - 滞在時間<br>" +
                    "• `개근` - 皆勤", schema = @Schema(implementation = RankingType.class), required = true) @RequestParam RankingType rankingType,
            @Parameter(description = "기간 타입 / 期間タイプ<br><br>" +
                    "• `일간` - 日間<br>" +
                    "• `주간` - 週間（月曜日〜日曜日）<br>" +
                    "• `월간` - 月間（今月）<br>" +
                    "• `전체` - 全体（すべての期間）", schema = @Schema(implementation = PeriodType.class), required = true) @RequestParam PeriodType periodType,
            @Parameter(description = "날짜 / 日付 (YYYY-MM-DD)", schema = @Schema(implementation = LocalDate.class), required = true) @RequestParam LocalDate startDate) {

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
