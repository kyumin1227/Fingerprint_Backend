package com.example.fingerprint_backend.command.Handler;

import com.example.fingerprint_backend.command.CommandHandler;
import com.example.fingerprint_backend.command.MessageCommand;
import com.example.fingerprint_backend.dto.clean.InfoResponse;
import com.example.fingerprint_backend.entity.LineEntity;
import com.example.fingerprint_backend.service.CleanOperationService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CleanCommandHandler implements CommandHandler {

    private final CleanOperationService cleanOperationService;

    public CleanCommandHandler(CleanOperationService cleanOperationService) {
        this.cleanOperationService = cleanOperationService;
    }

    @Override
    public MessageCommand getCommandType() {
        return MessageCommand.CLEAN;
    }

    @Override
    public String handleCommand(LineEntity line) {
        Long classId = line.getMember().getSchoolClass().getId();
        List<InfoResponse> cleanInfos = cleanOperationService.getCleanInfos(classId);
        Optional<InfoResponse> first = cleanInfos.stream()
                .filter(info -> info.getMembers().stream()
                        .anyMatch(member -> member.getStudentNumber().equals(line.getMember().getStudentNumber())))
                .findFirst();

        if (first.isPresent()) {
            InfoResponse info = first.get();
            StringBuilder response = new StringBuilder();
            response.append("다음 청소 예정입니다 😊\n\n");
            response.append("청소 구역: ").append(info.getCleanArea()).append("\n");
            response.append("청소 날짜: ").append(info.getDate()).append("\n");
            response.append("청소 인원: ").append(info.getMembers().size()).append("\n");
            response.append("청소 인원 목록: \n");
            info.getMembers().forEach(member ->
                    response.append(String.format("%s%s (%s)", member.getFamilyName(), member.getGivenName(), member.getStudentNumber())).append("\n"));
            return response.toString();
        }

        return "예정된 청소가 없습니다.";
    }

    @Override
    public String getHelpMessage() {
        return "청소: 자신의 다음 청소 예정을 표시합니다.";
    }
}
