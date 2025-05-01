package com.example.fingerprint_backend.command.Handler;

import com.example.fingerprint_backend.command.CommandHandler;
import com.example.fingerprint_backend.command.MessageCommand;
import com.example.fingerprint_backend.domain.fingerprint.dto.RankEntityDto;
import com.example.fingerprint_backend.domain.fingerprint.dto.RankingResponseDto;
import com.example.fingerprint_backend.domain.fingerprint.service.ranking.RankingApplicationService;
import com.example.fingerprint_backend.domain.fingerprint.types.PeriodType;
import com.example.fingerprint_backend.domain.fingerprint.types.RankingType;
import com.example.fingerprint_backend.entity.LineEntity;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WeeklyRankingHandler implements CommandHandler {

    private final RankingApplicationService rankingApplicationService;

    @Override
    public MessageCommand getCommandType() {
        return MessageCommand.WEEKLY_RANKING;
    }

    @Override
    public String handleCommand(LineEntity line) {
        RankingResponseDto rankingResponseDtoByArrival = rankingApplicationService.getRankingResponseDto(
                RankingType.등교_시간, PeriodType.주간, LocalDate.now(), 5);
        RankingResponseDto rankingResponseDtoByStay = rankingApplicationService.getRankingResponseDto(
                RankingType.체류_시간, PeriodType.주간, LocalDate.now(), 5);

        StringBuilder response = new StringBuilder();
        response.append("📊 주간 랭킹\n\n");

        response.append("⏰ 평균 등교 시간 순위\n");
        for (RankEntityDto rankEntityDto : rankingResponseDtoByArrival.rankList()) {
            response.append(rankEntityDto.getRank()).append("위 ").append(rankEntityDto.getFamilyName()).append(" ")
                    .append(rankEntityDto.getGivenName())
                    .append(" (").append(rankEntityDto.getFormattedValue()).append(")\n");
        }

        response.append("\n⏱️ 총 체류 시간 순위\n");
        for (RankEntityDto rankEntityDto : rankingResponseDtoByStay.rankList()) {
            response.append(rankEntityDto.getRank()).append("위 ").append(rankEntityDto.getFamilyName()).append(" ")
                    .append(rankEntityDto.getGivenName())
                    .append(" (").append(rankEntityDto.getFormattedValue()).append(")\n");
        }

        return response.toString();
    }

    @Override
    public String getHelpMessage() {
        return "주간 랭킹: 평균 등교 시간과 총 체류 시간 순위를 보여줍니다.";
    }
}