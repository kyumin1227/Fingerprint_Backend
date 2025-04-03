package com.example.fingerprint_backend.types;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.example.fingerprint_backend.entity.LineEntity;
import com.example.fingerprint_backend.repository.LineRepository;

public enum LineCommand {
    CLEAN(Arrays.asList("청소", "掃除", "clean")) {
        @Override
        public String execute(LineEntity line) {
            return line.getMember().getStudentNumber() + "의 청소 계획";
        }
    },
    HELLO(Arrays.asList("안녕", "こんにちは", "hello")) {
        @Override
        public String execute(LineEntity line) {
            return "안녕하세요 👋";
        }
    },
    DISCONNECT(Arrays.asList("연결해제", "切断", "disconnect")) {
        @Override
        public String execute(LineEntity line) {
            return "라인 연결이 해제되었습니다.";
        }
    };

    private final List<String> keywords;

    LineCommand(List<String> keywords) {
        this.keywords = keywords;
    }

    public abstract String execute(LineEntity line);

    public static Optional<LineCommand> fromKeyword(String keyword) {
        return Arrays.stream(values())
                .filter(cmd -> cmd.keywords.contains(keyword.toLowerCase()))
                .findFirst();
    }
}
