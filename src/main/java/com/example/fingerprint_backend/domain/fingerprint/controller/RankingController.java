package com.example.fingerprint_backend.domain.fingerprint.controller;

import com.example.fingerprint_backend.ApiResponse;
import com.example.fingerprint_backend.domain.fingerprint.dto.RankingResponseDto;
import com.example.fingerprint_backend.domain.fingerprint.service.ranking.RankingApplicationService;
import com.example.fingerprint_backend.domain.fingerprint.types.PeriodType;
import com.example.fingerprint_backend.domain.fingerprint.types.RankingType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/ranking")
@RequiredArgsConstructor
public class RankingController {

    private final RankingApplicationService rankingApplicationService;

    @GetMapping("")
    public ResponseEntity<ApiResponse> getRanking(
            @RequestParam RankingType rankingType,
            @RequestParam PeriodType periodType,
            @RequestParam LocalDate startDate
    ) {

        Integer limit = 5;

        RankingResponseDto ranking = rankingApplicationService.getRankingResponseDto(
                rankingType,
                periodType,
                startDate,
                limit
        );

        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse(true, "랭킹 조회 성공", ranking) // 랭킹 조회 성공
        );
    }
}
