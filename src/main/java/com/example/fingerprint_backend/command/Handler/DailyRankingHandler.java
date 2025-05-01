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
public class DailyRankingHandler implements CommandHandler {

    private final RankingApplicationService rankingApplicationService;

    @Override
    public MessageCommand getCommandType() {
        return MessageCommand.DAILY_RANKING;
    }

    @Override
    public String handleCommand(LineEntity line) {

        RankingResponseDto rankingResponseDtoByAteend = rankingApplicationService.getRankingResponseDto(
                RankingType.등교_시간, PeriodType.일간, LocalDate.now(), 5);

//        금일의 체류 시간은 아직 하교 하지 않았을 경우 정해지지 않았으므로, 전날의 체류 시간으로 대체
        RankingResponseDto rankingResponseDtoByStay = rankingApplicationService.getRankingResponseDto(
                RankingType.체류_시간, PeriodType.일간, LocalDate.now().minusDays(1), 5);

        StringBuilder response = new StringBuilder();
        response.append("📊 일간 랭킹\n\n");

        response.append("🏃 금일 등교 순위\n");
        for (RankEntityDto rankEntityDto : rankingResponseDtoByAteend.rankList()) {
            response.append(rankEntityDto.getRank()).append("위 ").append(rankEntityDto.getFamilyName()).append(" ").append(rankEntityDto.getGivenName())
                    .append(" (").append(rankEntityDto.getFormattedValue()).append(")\n");
        }

        response.append("\n⏱️ 전날 체류 시간 순위\n");
        for (RankEntityDto rankEntityDto : rankingResponseDtoByStay.rankList()) {
            response.append(rankEntityDto.getRank()).append("위 ").append(rankEntityDto.getFamilyName()).append(" ").append(rankEntityDto.getGivenName())
                    .append(" (").append(rankEntityDto.getFormattedValue()).append(")\n");
        }

        return response.toString();
    }

    @Override
    public String getHelpMessage() {
        return "일간 랭킹: 금일 등교 순위와 전날 체류 시간 순위를 보여줍니다.";
    }
}