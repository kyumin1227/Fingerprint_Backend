package com.example.fingerprint_backend.controller;

import com.example.fingerprint_backend.ApiResponse;
import com.example.fingerprint_backend.dto.GoogleRegisterDto;
import com.example.fingerprint_backend.dto.LoginResponse;
import com.example.fingerprint_backend.entity.MemberEntity;
import com.example.fingerprint_backend.service.AccountService;
import com.example.fingerprint_backend.service.AuthService;
import com.example.fingerprint_backend.jwt.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AccountService accountService;
    private final JWTUtil JWTUtil;

    @PostMapping("/api/login")
    public ResponseEntity<ApiResponse> login(HttpServletRequest request) {
        String googleIdToken = authService.extractGoogleIdToken(request.getHeader("Authorization"));
        String email = authService.verifyAndExtractGoogleEmail(googleIdToken);

//        이메일 형식 검증 (학교 이메일)
        authService.validateEmail(email);
        MemberEntity loginMember = authService.getMemberByEmail(email);

        String token = JWTUtil.generateToken(loginMember.getStudentNumber(), loginMember.getEmail());

        LoginResponse loginResponse = new LoginResponse(loginMember, token);

//        로그인 성공
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse(true, "로그인: 사용자 인증 성공", loginResponse));
    }

    @PostMapping("/api/register")
    public ResponseEntity<ApiResponse> register(HttpServletRequest request,
                                                @RequestBody GoogleRegisterDto googleRegisterDto) {

        String googleIdToken = authService.extractGoogleIdToken(request.getHeader("Authorization"));
        authService.verifyAndExtractGoogleEmail(googleIdToken);

        authService.validateRegisterInfo(googleRegisterDto);

        LoginResponse userInfo = authService.googleDecode(googleIdToken);

        authService.validateEmail(userInfo.getEmail());
        authService.validateEmailUnique(userInfo.getEmail());
        authService.validateStudentNumberUnique(googleRegisterDto.getStudentNumber());

        MemberEntity registered = authService.register(userInfo, googleRegisterDto);
        accountService.setSchoolClass(registered, googleRegisterDto.getClassName());

        String token = JWTUtil.generateToken(registered.getStudentNumber(), registered.getEmail());

        LoginResponse loginResponse = new LoginResponse(registered, token);

        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "회원가입 성공", loginResponse));
    }
}
