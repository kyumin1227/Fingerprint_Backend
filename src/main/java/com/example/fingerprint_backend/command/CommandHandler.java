package com.example.fingerprint_backend.command;

import com.example.fingerprint_backend.entity.LineEntity;

public interface CommandHandler {
    MessageCommand getCommandType();

    /**
     * 사용자가 보낸 메시지를 처리하는 메소드
     *
     * @param line LineEntity 객체
     * @return 답장할 메시지
     */
    String handleCommand(LineEntity line);
}
