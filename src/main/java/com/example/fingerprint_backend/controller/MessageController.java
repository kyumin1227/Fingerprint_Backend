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
        lineService.lineMessageHandler(request);
    }

}
