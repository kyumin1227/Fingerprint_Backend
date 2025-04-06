package com.example.fingerprint_backend;

import com.example.fingerprint_backend.service.LineService;
import com.example.fingerprint_backend.command.MessageCommand;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class MessageTest {

    @Autowired
    private LineService lineService;

    @DisplayName("메시지로 들어오는 키워드 변환 테스트")
    @Test
    void commandTest() {
        String keyword = "청소";
        MessageCommand Command = MessageCommand.fromKeyword(keyword)
                .orElseThrow();

        assertThat(Command).isEqualTo(MessageCommand.CLEAN);
    }

    @DisplayName("메시지로 들어오는 키워드 변환 테스트")
    @Test
    void commandTest2() {
        String keyword = "연결해제";
        MessageCommand Command = MessageCommand.fromKeyword(keyword)
                .orElseThrow();

        assertThat(Command).isEqualTo(MessageCommand.DISCONNECT);
    }
}
