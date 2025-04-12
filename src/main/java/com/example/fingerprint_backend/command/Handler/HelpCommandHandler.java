package com.example.fingerprint_backend.command.Handler;

import com.example.fingerprint_backend.command.CommandHandler;
import com.example.fingerprint_backend.command.MessageCommand;
import com.example.fingerprint_backend.entity.LineEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class HelpCommandHandler implements CommandHandler {

    private final List<String> commandList;

    public HelpCommandHandler(List<CommandHandler> handlers) {
        commandList = handlers.stream()
                .map(CommandHandler::getHelpMessage)
                .collect(Collectors.toList());
    }

    @Override
    public MessageCommand getCommandType() {
        return MessageCommand.HELP;
    }

    @Override
    public String handleCommand(LineEntity line) {
        StringBuilder commandList = new StringBuilder("지원하는 명령어 목록:\n\n");
        for (String command : this.commandList) {
            commandList.append(command).append("\n");
        }
        return commandList.toString();
    }

    @Override
    public String getHelpMessage() {
        return "도움말: 명령어 정보를 표시합니다.";
    }
}
