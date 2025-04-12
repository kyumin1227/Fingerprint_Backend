package com.example.fingerprint_backend.command.Handler;

import com.example.fingerprint_backend.command.CommandHandler;
import com.example.fingerprint_backend.command.MessageCommand;
import com.example.fingerprint_backend.entity.LineEntity;
import org.springframework.stereotype.Component;

@Component
public class UserInfoCommandHandler implements CommandHandler {
    @Override
    public MessageCommand getCommandType() {
        return MessageCommand.USER_INFO;
    }

    @Override
    public String handleCommand(LineEntity line) {
        StringBuilder response = new StringBuilder();
        response.append("연결된 유저 정보\n\n");
        response.append("이름: ").append(line.getMember().getFamilyName()).append(" ").append(line.getMember().getGivenName()).append("\n");
        response.append("학번: ").append(line.getMember().getStudentNumber()).append("\n");
        response.append("학급: ").append(line.getMember().getSchoolClass().getName()).append("\n");
        response.append("이메일: ").append(line.getMember().getEmail()).append("\n");
        response.append("가입 일자: ").append(line.getMember().getRegisterTime()).append("\n");

        return response.toString();
    }

    @Override
    public String getHelpMessage() {
        return "유저 정보: 연결된 유저의 정보를 표시합니다.";
    }
}
