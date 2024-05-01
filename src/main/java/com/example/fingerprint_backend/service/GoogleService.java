package com.example.fingerprint_backend.service;

import com.example.fingerprint_backend.dto.GoogleLoginUserInfoDto;
import com.example.fingerprint_backend.dto.GoogleRegisterDto;
import com.example.fingerprint_backend.entity.MemberEntity;
import com.example.fingerprint_backend.repository.MemberRepository;
import com.example.fingerprint_backend.types.MemberLanguage;
import com.example.fingerprint_backend.types.MemberRole;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GoogleService {

    final private MemberRepository memberRepository;

//    credential 키를 받아서 정보를 추출 후 dto에 이름과 이메일을 담아 반환
    public GoogleLoginUserInfoDto googleDecode(String credential) {
//        jwt의 body 부분을 decode
        String[] splitParts = credential.split("\\.");
        String base64EncodedBody = splitParts[1];
        String body = new String(Base64.getDecoder().decode(base64EncodedBody));

        System.out.println("JWT Body: " + body);

//        json에서 name과 email 값을 추출
        JSONObject jsonObject = new JSONObject(body);
        GoogleLoginUserInfoDto userInfoDto = new GoogleLoginUserInfoDto();
        userInfoDto.setName(jsonObject.getString("name"));
        userInfoDto.setEmail(jsonObject.getString("email"));
        userInfoDto.setPicture(jsonObject.getString("picture"));
        userInfoDto.setExp(jsonObject.optLong("exp"));


        System.out.println("userInfoDto.getEmail() = " + userInfoDto.getEmail());
        System.out.println("userInfoDto.getName() = " + userInfoDto.getName());
        System.out.println("userInfoDto.getPicture() = " + userInfoDto.getPicture());
        System.out.println("userInfoDto.getExp() = " + userInfoDto.getExp());

        return userInfoDto;
    }

//    가입이 되어있는 유저인지 확인 (회원 = true, 비회원 = false)
    public Boolean isUser(GoogleLoginUserInfoDto userInfoDto) {
        Optional<MemberEntity> user = memberRepository.findByEmail(userInfoDto.getEmail());

        if (user.isEmpty()) {
            return false;
        } else {
            return true;
        }

    }

//    회원가입
    public MemberEntity register(GoogleRegisterDto info) {
        MemberEntity member = new MemberEntity(info.getStudentNum(), info.getName(), info.getEmail(), info.getKakao(), MemberLanguage.KOREA, MemberRole.Student , LocalDateTime.now());
        MemberEntity save = memberRepository.save(member);

        return save;
    }

//    토큰의 유효기간 확인 후 사용 가능 토큰이면 true 불가능이면 false 반환
    public Boolean expCheck(Long exp) {

        ZonedDateTime givenDateTime = ZonedDateTime.ofInstant(Instant.ofEpochSecond(exp), ZoneId.systemDefault());
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());

        if (givenDateTime.isBefore(now)) {
            return false;
        } else {
            return true;
        }
    }
}
