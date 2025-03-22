package com.example.fingerprint_backend.controller;

import com.example.fingerprint_backend.ApiResponse;
import com.example.fingerprint_backend.dto.message.LineWebhookRequest;
import com.example.fingerprint_backend.entity.MemberEntity;
import com.example.fingerprint_backend.jwt.CustomUserDetails;
import com.example.fingerprint_backend.jwt.JWTUtil;
import com.example.fingerprint_backend.service.GetService;
import com.example.fingerprint_backend.service.LineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/message")
@RequiredArgsConstructor
public class MessageController {

    private final JWTUtil jwtUtil;
    private final LineService lineService;
    private final GetService getService;

    @GetMapping("")
    public ResponseEntity<ApiResponse> createJwtToken(@AuthenticationPrincipal CustomUserDetails user) {

        String token = jwtUtil.generateToken(user.getUsername(), null, 600000);
        return ResponseEntity.ok(new ApiResponse(true, "Token created", token));
    }

    @PostMapping("/line")
    public void receiveLineMessage(@RequestBody LineWebhookRequest request) {
        String text = request.getEvents().get(0).getMessage().getText();
        String userId = request.getEvents().get(0).getSource().getUserId();
        String replyToken = request.getEvents().get(0).getReplyToken();

        if (!lineService.isLineIdExist(userId)) {
//           등록된 라인 아이디가 아닐 경우
            if (!jwtUtil.validateToken(text)) {
                lineService.sendReply(replyToken, "Invalid token");
                return;
            }
        };

//        등록된 라인 아이디일 경우

        String studentNumber = jwtUtil.getStudentNumberFromToken(text);

        MemberEntity member = getService.getMemberByStudentNumber(studentNumber);

        System.out.println("Received message from LINE: " + text);
        System.out.println("User ID: " + userId);
        lineService.sendReply(replyToken, "Message received");
    }

}
