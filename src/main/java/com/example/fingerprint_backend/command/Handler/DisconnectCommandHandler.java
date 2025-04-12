package com.example.fingerprint_backend.command.Handler;

import com.example.fingerprint_backend.command.CommandHandler;
import com.example.fingerprint_backend.command.MessageCommand;
import com.example.fingerprint_backend.entity.LineEntity;
import com.example.fingerprint_backend.repository.LineRepository;
import org.springframework.stereotype.Component;

@Component
public class DisconnectCommandHandler implements CommandHandler {

    private final LineRepository lineRepository;

    public DisconnectCommandHandler(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    @Override
    public MessageCommand getCommandType() {
        return MessageCommand.DISCONNECT;
    }

    @Override
    public String handleCommand(LineEntity line) {
        lineRepository.delete(line);

        return String.format("%s%s (%s)\n연결이 해제되었습니다.", line.getMember().getFamilyName(), line.getMember().getGivenName(), line.getMember().getStudentNumber());
    }

    @Override
    public String getHelpMessage() {
        return "연결 해제: 라인 계정 연결을 해제합니다.";
    }
}
