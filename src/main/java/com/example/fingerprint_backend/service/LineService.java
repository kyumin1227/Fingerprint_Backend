package com.example.fingerprint_backend.service;

import com.example.fingerprint_backend.entity.LineEntity;
import com.example.fingerprint_backend.entity.MemberEntity;
import com.example.fingerprint_backend.repository.LineRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Transactional
public class LineService {

    @Value("${LINE_ACCESS_TOKEN}")
    private String LINE_ACCESS_TOKEN;

    private final String LINE_REPLY_URL = "https://api.line.me/v2/bot/message/reply";
    private final RestTemplate restTemplate;
    private final LineRepository lineRepository;

    public LineService(RestTemplate restTemplate, LineRepository lineRepository) {
        this.restTemplate = restTemplate;
        this.lineRepository = lineRepository;
    }

    public void sendReply(String replyToken, String message) {
        System.out.println("LINE_ACCESS_TOKEN = " + LINE_ACCESS_TOKEN);
        System.out.println(restTemplate);
        System.out.println("replyToken = " + replyToken);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + LINE_ACCESS_TOKEN);

        String requestBody = "{"
                + "\"replyToken\":\"" + replyToken + "\","
                + "\"messages\":[{\"type\":\"text\",\"text\":\"" + message + "\"}]"
                + "}";

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        restTemplate.postForObject(LINE_REPLY_URL, requestEntity, String.class);
    }

    /**
     * 유저의 라인 아이디를 데이터베이스에 저장
     * @param token 유저의 토큰
     * @param lineId 라인 아이디
     */
    public void createLine(String token, String lineId) {
//        LineEntity lineEntity = new LineEntity(, lineId);
    }

    public boolean isLineIdExist(String lineId) {
        return lineRepository.existsByLineId(lineId);
    }
}
