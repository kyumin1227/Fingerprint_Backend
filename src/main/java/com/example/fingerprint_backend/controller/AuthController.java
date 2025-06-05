package com.example.fingerprint_backend.controller;

import com.example.fingerprint_backend.ApiResult;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Authentication", description = "인증 API / 認証API<br>Authorization 헤더에 Google ID 토큰을 포함해야 합니다。/ AuthorizationヘッダーにGoogle IDトークンを含める必要があります。")
@RestController
@RequiredArgsConstructor
public class AuthController {

        private final AuthService authService;
        private final AccountService accountService;
        private final JWTUtil JWTUtil;

        @Operation(operationId = "login", summary = "로그인 / ログイン", description = "사용자를 인증하고 로그인합니다。<br>ユーザーを認証し、ログインします。")
        @PostMapping("/api/login")
        public ResponseEntity<ApiResult> login(HttpServletRequest request) {
                String googleIdToken = authService.extractGoogleIdToken(request.getHeader("Authorization"));
                String email = authService.verifyAndExtractGoogleEmail(googleIdToken);

                // 이메일 형식 검증 (학교 이메일)
                authService.validateEmail(email);
                MemberEntity loginMember = authService.getMemberByEmail(email);

                String token = JWTUtil.generateToken(loginMember.getStudentNumber(), loginMember.getEmail());

                LoginResponse loginResponse = new LoginResponse(loginMember, token);

                // 로그인 성공
                return ResponseEntity.status(HttpStatus.OK)
                                .body(new ApiResult(true, "로그인: 사용자 인증 성공", loginResponse));
        }

        @Operation(operationId = "register", summary = "회원가입 / 会員登録", description = "추가 정보를 사용하여 새로운 사용자를 등록합니다。<br>追加情報を使用して新しいユーザーを登録します。")
        @PostMapping("/api/register")
        public ResponseEntity<ApiResult> register(HttpServletRequest request,
                        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "회원가입 요청 본문 / 会員登録リクエストボディ<br><br>"
                                        +
                                        "• `studentNumber` - 학번 / 学籍番号<br>" +
                                        "• `className` - 반 이름 / クラス名") @RequestBody GoogleRegisterDto googleRegisterDto) {

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

                return ResponseEntity.status(HttpStatus.OK).body(new ApiResult(true, "회원가입 성공", loginResponse));
        }
}
