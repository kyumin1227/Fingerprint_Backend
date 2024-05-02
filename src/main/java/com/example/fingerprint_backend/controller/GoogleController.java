package com.example.fingerprint_backend.controller;

import com.example.fingerprint_backend.ApiResponse;
import com.example.fingerprint_backend.dto.GoogleLoginDto;
import com.example.fingerprint_backend.dto.GoogleLoginUserInfoDto;
import com.example.fingerprint_backend.dto.GoogleRegisterDto;
import com.example.fingerprint_backend.entity.MemberEntity;
import com.example.fingerprint_backend.service.GoogleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
@RequiredArgsConstructor
public class GoogleController {

    final private GoogleService googleService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody GoogleLoginDto googleLoginDto) throws GeneralSecurityException, IOException {
        String credential = googleLoginDto.getCredential();
        System.out.println("credential = " + credential);

//        최초 로그인 시 토큰 진위여부 검증
        if (!googleService.googleTokenCheck(credential)) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse(false, "로그인 실패: 올바르지 않은 토큰입니다.", null));
        }

        GoogleLoginUserInfoDto userInfoDto = googleService.googleDecode(credential);

//        영진 전문대 이메일 여부 확인
        if (!userInfoDto.getEmail().endsWith("@g.yju.ac.kr")) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse(false, "거부: 영진 전문대 학생이 아닙니다. \n @g.yju.ac.kr 이메일을 이용해주세요", null));
        }

//        회원이 아닌 경우
        if (!googleService.isUserByEmail(userInfoDto)) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse(false, "가입 필요: 학번 및 카카오톡 아이디 등록이 필요합니다.", userInfoDto));
        }

//        로그인 성공
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse(true, "로그인: 사용자 인증 성공", userInfoDto));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody GoogleRegisterDto googleRegisterDto) throws GeneralSecurityException, IOException {
        String credential = googleRegisterDto.getCredential();
        String email = googleRegisterDto.getEmail();
        String name = googleRegisterDto.getName();
        String studentNum = googleRegisterDto.getStudentNum();
        String kakao = googleRegisterDto.getKakao();

//        최초 회원가입 시 토큰 진위여부 검증
        if (!googleService.googleTokenCheck(credential)) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse(false, "회원가입 실패: 올바르지 않은 토큰입니다.", null));
        }

        GoogleLoginUserInfoDto userInfoDto = googleService.googleDecode(credential);

//        토큰의 정보와 이메일, 이름 정보가 일치하지 않는 경우
        if (!userInfoDto.getName().equals(name) || !userInfoDto.getEmail().equals(email)) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(false, "회원가입 실패: 토큰 정보가 일치하지 않습니다.", null));
        }

//        이미 가입된 이메일일 경우
        if (googleService.isUserByEmail(userInfoDto)) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(false, "회원가입 실패: 이미 가입된 이메일입니다.", null));
        }

//        이미 가입된 학번인 경우
        if (googleService.isUserByStdNum(studentNum)) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(false, "회원가입 실패: 이미 가입된 학번입니다.", null));
        }

//        이미 가입된 카카오톡 계정인 경우
        if (googleService.isUserByKakao(kakao)) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(false, "회원가입 실패: 이미 가입된 카카오톡 계정입니다.", null));
        }

        MemberEntity registered = googleService.register(googleRegisterDto);

        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "회원가입 성공", registered));
    }
}
