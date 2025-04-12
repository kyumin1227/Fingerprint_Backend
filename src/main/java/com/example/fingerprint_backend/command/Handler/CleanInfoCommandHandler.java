package com.example.fingerprint_backend.command.Handler;

import com.example.fingerprint_backend.command.CommandHandler;
import com.example.fingerprint_backend.command.MessageCommand;
import com.example.fingerprint_backend.entity.LineEntity;
import org.springframework.stereotype.Component;

@Component
public class CleanInfoCommandHandler implements CommandHandler {


    @Override
    public MessageCommand getCommandType() {
        return MessageCommand.CLEAN_INFO;
    }

    @Override
    public String handleCommand(LineEntity line) {
        StringBuilder response = new StringBuilder();
        response.append("청소 정보 확인하기\n\n");
//        TODO : 반에 따라 URL 다르게 설정하기
//        response.append(baseurl + "/cleaninfo?classId=" + line.getMember().getSchoolClass().getId()).append("\n");
        response.append("https://bannote.org" + "/src/pages/clean/clean.html");

        return response.toString();
    }

    @Override
    public String getHelpMessage() {
        return "청소 정보: 청소 정보의 URL을 표시합니다.";
    }


}
