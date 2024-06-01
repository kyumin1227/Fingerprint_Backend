package com.example.fingerprint_backend.service;

import com.example.fingerprint_backend.dto.KakaoDto;
import com.example.fingerprint_backend.entity.KakaoEntity;
import com.example.fingerprint_backend.repository.KakaoRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KakaoService {

    private final KakaoRepository kakaoRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${KAKAO_CLIENT_ID}")
    private String kakaoClientId;

    public Boolean setKakaoToken(KakaoDto kakaoDto) {

        String url = "https://kauth.kakao.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoClientId);
        body.add("redirect_uri", kakaoDto.getRedirect_uri());
        body.add("code", kakaoDto.getCode());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        System.out.println("response.getBody() = " + response.getBody());

        System.out.println("kakaoDto.getStudentNumber() = " + kakaoDto.getStudentNumber());

        Boolean b = setKakaoTokenToEntity(response.getBody(), kakaoDto.getStudentNumber());

        if (b) {
            return true;
        } else {
            return false;
        }
    }

    private Boolean setKakaoTokenToEntity(String body, String studentNumber) {

        JSONObject jsonObject = new JSONObject(body);

        String accessToken = jsonObject.get("access_token").toString();
        String refreshToken = jsonObject.get("refresh_token").toString();
        String scope = jsonObject.get("scope").toString();

        LocalDateTime now = LocalDateTime.now();

        String profileId = getKakaoId(accessToken);

        KakaoEntity kakaoEntity = new KakaoEntity(studentNumber, false, null, accessToken, refreshToken, scope, profileId, now);

        KakaoEntity saved = kakaoRepository.save(kakaoEntity);

        if (saved.getStudentNumber().equals(studentNumber)) {
            return true;
        } else {
            return false;
        }

    }

    private String getKakaoId(String accessToken) {

        String url = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

        JSONObject jsonObject = new JSONObject(response.getBody());

        String profileId = jsonObject.get("id").toString();

        return profileId;
    }
}
