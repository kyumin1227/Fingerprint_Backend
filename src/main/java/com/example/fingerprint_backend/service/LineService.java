package com.example.fingerprint_backend.service;

import com.example.fingerprint_backend.command.CommandService;
import com.example.fingerprint_backend.dto.message.LineWebhookRequest;
import com.example.fingerprint_backend.entity.LineEntity;
import com.example.fingerprint_backend.entity.MemberEntity;
import com.example.fingerprint_backend.jwt.JWTUtil;
import com.example.fingerprint_backend.repository.LineRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class LineService {

    private final JWTUtil jwtUtil;
    private final GetService getService;
    private final CommandService commandService;

    @Value("${LINE_ACCESS_TOKEN}")
    private String LINE_ACCESS_TOKEN;

    private final String LINE_REPLY_URL = "https://api.line.me/v2/bot/message/reply";
    private final RestTemplate restTemplate;
    private final LineRepository lineRepository;

    public LineService(JWTUtil jwtUtil, GetService getService, RestTemplate restTemplate, LineRepository lineRepository, CommandService commandService) {
        this.jwtUtil = jwtUtil;
        this.getService = getService;
        this.restTemplate = restTemplate;
        this.lineRepository = lineRepository;
        this.commandService = commandService;
    }

    public void sendMessages(String lineId, String message) {
        LineEntity lineEntity = lineRepository.findByLineId(lineId)
                .orElseThrow(() -> new IllegalArgumentException("해당 라인 아이디가 존재하지 않습니다."));
        sendReply(lineEntity.getLineId(), message);
    }

    /**
     * 라인으로 답장을 보내는 메소드
     *
     * @param replyToken 라인에서 받은 토큰
     * @param message    보낼 메시지
     */
    public void sendReply(String replyToken, String message) {
        System.out.println("LINE_ACCESS_TOKEN = " + LINE_ACCESS_TOKEN);
        System.out.println(restTemplate);
        System.out.println("replyToken = " + replyToken);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + LINE_ACCESS_TOKEN);

        try {
            Map<String, Object> body = new HashMap<>();
            body.put("replyToken", replyToken);

            Map<String, String> textMessage = new HashMap<>();
            textMessage.put("type", "text");
            textMessage.put("text", message);

            body.put("messages", List.of(textMessage));

            ObjectMapper mapper = new ObjectMapper();
            String requestBody = mapper.writeValueAsString(body);

            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
            restTemplate.postForObject(LINE_REPLY_URL, requestEntity, String.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 라인 메시지를 처리하는 메소드
     *
     * @param request 라인 웹훅 요청
     */
    public void lineMessageHandler(LineWebhookRequest request) {
        String lineUserId = request.getEvents().get(0).getSource().getUserId();
        String text = request.getEvents().get(0).getMessage().getText();
        String replyToken = request.getEvents().get(0).getReplyToken();

        if (!isLineIdExist(lineUserId)) {
//          등록되지 않은 라인 아이디일 경우
            guestUserHandler(lineUserId, text, replyToken);
            return;
        }
//        등록된 라인 아이디일 경우
        LineEntity line = getLineByLineId(lineUserId);

        String replyMessage = commandService.getReply(text, line);

        sendReply(replyToken, replyMessage);
    }

    /**
     * 라인 아이디가 등록되지 않은 경우의 처리
     *
     * @param lineUserId 라인 아이디
     * @param token      토큰 (메시지 내용)
     * @param replyToken 라인 답장 토큰
     */
    public void guestUserHandler(String lineUserId, String token, String replyToken) {
        if (!jwtUtil.validateToken(token)) {
//                토큰이 유효하지 않을 경우
            sendReply(replyToken, "등록되지 않은 계정입니다.\n토큰을 발급하여 계정 연결을 해주세요.\nhttps://bannote.org");
            return;
        }
//            계정 연결
        try {
            String studentNumberFromToken = jwtUtil.getStudentNumberFromToken(token);
            createLine(studentNumberFromToken, lineUserId);
            sendReply(replyToken, "학번: " + studentNumberFromToken + "번\n계정 연결이 완료되었습니다.");
        } catch (Exception e) {
            sendReply(replyToken, "계정 연결에 실패했습니다.");
        }
    }

    /**
     * 유저의 라인 아이디를 데이터베이스에 저장
     *
     * @param studentNumber 학번
     * @param lineId        라인 아이디
     */
    public void createLine(String studentNumber, String lineId) {
        MemberEntity member = getService.getMemberByStudentNumber(studentNumber);
        LineEntity lineEntity = new LineEntity(member, lineId);
        lineRepository.save(lineEntity);
    }

    public void deleteLine(String lineId) {
        LineEntity lineEntity = lineRepository.findByLineId(lineId)
                .orElseThrow(() -> new IllegalArgumentException("해당 라인 아이디가 존재하지 않습니다."));
        lineRepository.delete(lineEntity);
    }

    public boolean isLineIdExist(String lineId) {
        return lineRepository.existsByLineId(lineId);
    }

    public LineEntity getLineByLineId(String lineId) {
        return lineRepository.findByLineId(lineId)
                .orElseThrow(() -> new IllegalArgumentException("해당 라인 아이디가 존재하지 않습니다."));
    }
}
