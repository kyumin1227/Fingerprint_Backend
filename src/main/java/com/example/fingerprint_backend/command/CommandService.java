package com.example.fingerprint_backend.command;

import com.example.fingerprint_backend.entity.LineEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CommandService {

    private final Map<MessageCommand, CommandHandler> commandHandlers;

    public CommandService(List<CommandHandler> handlers) {
        this.commandHandlers = handlers.stream()
                .collect(Collectors.toMap(CommandHandler::getCommandType, Function.identity()));
    }

    /**
     * 사용자가 보낸 메시지를 처리하는 메소드
     *
     * @param text 사용자가 보낸 메시지
     * @param line LineEntity 객체
     * @return 처리된 메시지
     */
    public String getReply(String text, LineEntity line) {
        CommandHandler commandHandler = MessageCommand.fromKeyword(text)
                .map(commandHandlers::get)
                .orElse(null);

        if (commandHandler == null) {
            return "지원하지 않는 명령어입니다.";
        }

        return commandHandler.handleCommand(line);
    }
}
