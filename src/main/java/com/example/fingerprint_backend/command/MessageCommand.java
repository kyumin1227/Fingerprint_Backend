package com.example.fingerprint_backend.command;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 지원하는 명령어를 관리하는 클래스
 */
public enum MessageCommand {
    CLEAN(List.of("청소", "掃除", "clean")),
    HELLO(List.of("안녕", "こんにちは", "hello")),
    DISCONNECT(List.of("연결해제", "切断", "disconnect")),
    CLEAN_INFO(List.of("청소정보", "청소일정", "掃除情報", "cleaninfo"));

    private final List<String> keywords;

    MessageCommand(List<String> keywords) {
        this.keywords = keywords;
    }

    public static Optional<MessageCommand> fromKeyword(String keyword) {
        return Arrays.stream(values())
                .filter(cmd -> cmd.keywords.contains(keyword.toLowerCase().replace(" ", "")))
                .findFirst();
    }
}
