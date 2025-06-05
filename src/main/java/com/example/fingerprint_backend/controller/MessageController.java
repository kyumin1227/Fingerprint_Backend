package com.example.fingerprint_backend.controller;

import com.example.fingerprint_backend.ApiResult;
import com.example.fingerprint_backend.dto.message.LineWebhookRequest;
import com.example.fingerprint_backend.jwt.CustomUserDetails;
import com.example.fingerprint_backend.jwt.JWTUtil;
import com.example.fingerprint_backend.service.LineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Messaging", description = "메시징 및 챗봇 API / メッセージングおよびチャットボットAPI")
@RestController
@RequestMapping("/api/message")
@RequiredArgsConstructor
public class MessageController {

    private final JWTUtil jwtUtil;
    private final LineService lineService;

    @Operation(operationId = "createJwtToken", summary = "JWT 토큰 생성 / JWTトークン生成", description = "사용자 인증을 위한 JWT 토큰을 생성합니다。/ ユーザー認証のためのJWTトークンを生成します。")
    @GetMapping("")
    public ResponseEntity<ApiResult> createJwtToken(@AuthenticationPrincipal CustomUserDetails user) {

        String token = jwtUtil.generateToken(user.getUsername(), null, 600000);
        return ResponseEntity.ok(new ApiResult(true, "Token created", token));
    }

    @Operation(operationId = "receiveLineMessage", summary = "LINE 메시지 수신 / LINEメッセージ受信", description = "LINE 웹훅 요청을 처리합니다。/ LINEウェブフックリクエストを処理します。")
    @PostMapping("/line")
    public void receiveLineMessage(@RequestBody LineWebhookRequest request) {
        lineService.lineMessageHandler(request);
    }

}
