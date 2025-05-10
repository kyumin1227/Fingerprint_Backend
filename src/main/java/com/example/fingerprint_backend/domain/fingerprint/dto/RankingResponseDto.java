package com.example.fingerprint_backend.domain.fingerprint.dto;

import com.example.fingerprint_backend.domain.fingerprint.types.PeriodType;
import com.example.fingerprint_backend.domain.fingerprint.types.RankingType;

import java.time.LocalDate;
import java.util.List;

public record RankingResponseDto(
                RankingType rankingType,
                PeriodType periodType,
                LocalDate startDate,
                List<RankEntityDto> rankList) {
}
