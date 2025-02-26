package com.example.fingerprint_backend.service;

import com.example.fingerprint_backend.dto.GoogleRegisterDto;
import com.example.fingerprint_backend.dto.LoginResponse;
import com.example.fingerprint_backend.entity.MemberEntity;
import com.example.fingerprint_backend.repository.MemberRepository;
import com.example.fingerprint_backend.repository.SchoolClassRepository;
import com.example.fingerprint_backend.types.MemberLanguage;
import com.example.fingerprint_backend.types.MemberRole;
import com.google.api.client.http.javanet.NetHttpTransport;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Collections;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.json.jackson2.JacksonFactory;


@Service
@RequiredArgsConstructor
public class AuthService {
    // 구글 로그인 및 회원가입 관련 서비스

    final private MemberRepository memberRepository;
    private final SchoolClassRepository schoolClassRepository;

    @Value("${GOOGLE_CLIENT_ID}")
    private String googleClientId;

    private GoogleIdTokenVerifier verifier;

    @PostConstruct
    public void init() {
        verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
                .setAudience(Collections.singletonList(googleClientId))
                .build();
    }

    /**
     * Authorization 헤더에서 Bearer 토큰 추출
     *
     * @return Google id 토큰
     * @throws AuthenticationCredentialsNotFoundException 토큰이 비어있거나 Bearer로 시작하지 않는 경우
     */
    public String extractGoogleIdToken(String credential) {
        if (credential == null || !credential.startsWith("Bearer ")) {
            throw new AuthenticationCredentialsNotFoundException("Authorization 헤더가 누락되었거나 올바르지 않습니다.");
        }
        return credential.substring(7);
    }

    /**
     * 구글 토큰 검증 및 이메일 추출
     *
     * @return 이메일
     * @throws AuthenticationCredentialsNotFoundException 토큰이 올바르지 않은 경우
     */
    public String verifyAndExtractGoogleEmail(String credential) {
        try {
            GoogleIdToken idToken = verifier.verify(credential);
            GoogleIdToken.Payload payload = idToken.getPayload();
            return payload.getEmail();
        } catch (Exception e) {
            throw new AuthenticationCredentialsNotFoundException("올바르지 않은 토큰입니다.");
        }
    }

    /**
     * 영진 전문대 이메일 여부 확인
     *
     * @param email 이메일
     * @throws AuthenticationCredentialsNotFoundException 영진 전문대 이메일이 아닌 경우
     */
    public void validateEmail(String email) {
        if (!email.endsWith("@g.yju.ac.kr")) {
            throw new AuthenticationCredentialsNotFoundException("거부: 영진 전문대 학생이 아닙니다. @g.yju.ac.kr 이메일을 이용해주세요");
        }
    }

    /**
     * 이메일로 회원 정보 조회
     *
     * @param email 이메일
     * @return 회원 정보
     * @throws AuthenticationCredentialsNotFoundException 미등록 이메일인 경우
     */
    public MemberEntity getMemberByEmail(String email) {
        System.out.println("email = " + email);
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationCredentialsNotFoundException("가입 필요: 학번 등록이 필요합니다."));
    }

    /**
     * 구글 토큰 디코딩
     *
     * @param credential
     * @return
     */
    public LoginResponse googleDecode(String credential) {
//        jwt의 body 부분을 decode
        String[] splitParts = credential.split("\\.");
        String base64EncodedBody = splitParts[1];
        String body = new String(Base64.getUrlDecoder().decode(base64EncodedBody));

        System.out.println("JWT Body: " + body);

//        json에서 정보 추출
        JSONObject jsonObject = new JSONObject(body);
        LoginResponse userInfo = new LoginResponse();
        userInfo.setName(jsonObject.getString("name"));
        userInfo.setEmail(jsonObject.getString("email"));
        userInfo.setGivenName(jsonObject.getString("given_name"));
        userInfo.setFamilyName(jsonObject.getString("family_name"));
        userInfo.setProfileImage(jsonObject.getString("picture"));

        return userInfo;
    }

    /**
     * 이메일 중복 확인
     *
     * @param email 이메일
     * @throws AuthenticationCredentialsNotFoundException 중복된 이메일인 경우
     */
    public void validateEmailUnique(String email) {
        memberRepository.findByEmail(email)
                .ifPresent(member -> {
                    throw new AuthenticationCredentialsNotFoundException("이미 가입된 이메일입니다.");
                });
    }

    /**
     * 학번 중복 확인
     *
     * @param studentNumber 학번
     * @throws AuthenticationCredentialsNotFoundException 중복된 학번인 경우
     */
    public void validateStudentNumberUnique(String studentNumber) {
        memberRepository.findByStudentNumber(studentNumber)
                .ifPresent(member -> {
                    throw new AuthenticationCredentialsNotFoundException("이미 가입된 학번입니다.");
                });
    }

    public void validateRegisterInfo(GoogleRegisterDto info) {
        if (info.getStudentNumber().isEmpty() || info.getClassName().isEmpty()) {
            throw new AuthenticationCredentialsNotFoundException("학번과 반을 입력해주세요.");
        }

        if (schoolClassRepository.findSchoolClassByName(info.getClassName()).isEmpty()) {
            throw new AuthenticationCredentialsNotFoundException("존재하지 않는 반입니다.");
        }
    }

    /**
     * 회원가입
     *
     * @param userInfo 구글 로그인 정보
     * @param info     회원가입 정보
     * @return 회원 정보
     */
    public MemberEntity register(LoginResponse userInfo, GoogleRegisterDto info) {
        MemberEntity member = new MemberEntity(
                info.getStudentNumber(),
                userInfo.getEmail(),
                userInfo.getName(),
                userInfo.getGivenName(),
                userInfo.getFamilyName(),
                MemberLanguage.KOREA,
                userInfo.getProfileImage()
        );

        member.addRole(MemberRole.STUDENT);

        return memberRepository.save(member);
    }

}
