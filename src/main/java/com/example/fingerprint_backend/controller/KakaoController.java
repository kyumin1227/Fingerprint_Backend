package com.example.fingerprint_backend.controller;

import com.example.fingerprint_backend.ApiResponse;
import com.example.fingerprint_backend.dto.KakaoDto;
import com.example.fingerprint_backend.service.GoogleService;
import com.example.fingerprint_backend.service.KakaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class KakaoController {

    private final KakaoService kakaoService;

    @PostMapping("/api/kakao")
    public ResponseEntity<ApiResponse> getKakaoToken(@RequestBody KakaoDto kakaoDto) {
        System.out.println("kakaoDto.getCode() = " + kakaoDto.getCode());
        System.out.println("kakaoDto.getRedirect_uri() = " + kakaoDto.getRedirect_uri());
        System.out.println("kakaoDto.getStudentNumber() = " + kakaoDto.getStudentNumber());

        Boolean result = kakaoService.setKakaoToken(kakaoDto);

        if (result) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "토큰 세팅 완료", null));
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(false, "토큰 세팅 실패", null));
        }

    }
}
