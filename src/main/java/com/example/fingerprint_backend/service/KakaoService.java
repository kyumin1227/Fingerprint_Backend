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

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

        System.out.println("카카오 엔티티 저장");
        System.out.println("studentNumber = " + studentNumber);
        System.out.println("accessToken = " + accessToken);
        System.out.println("refreshToken = " + refreshToken);
        System.out.println("scope = " + scope);
        System.out.println("profileId = " + profileId);
        System.out.println("now = " + now);

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

    /**
     * 리프레쉬 토큰을 통해 액세스 토큰을 재발급 받는 메소드
     * @param refreshToken
     * @return
     */
    public String getAccessToken(String refreshToken) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://kauth.kakao.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("client_id", kakaoClientId);
        body.add("refresh_token", refreshToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        System.out.println("response.getBody() = " + response.getBody());

        JSONObject jsonObject = new JSONObject(response.getBody());
        String accessToken = jsonObject.get("access_token").toString();

        return accessToken;
    }

    public Boolean sendKakaoMessage(String accessToken, String message, String uuid) {

        String url = "https://kapi.kakao.com/v1/api/talk/friends/message/default/send";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Bearer " + accessToken);

        JSONObject templateObject = new JSONObject();
        templateObject.put("object_type", "text");
        templateObject.put("text", message);
        templateObject.put("link", new JSONObject().put("web_url", "https://gsc-fingerprint.org"));

        // URL 인코딩을 합니다.
        String encodedReceiverUuids = URLEncoder.encode("[\"" + uuid + "\"]", StandardCharsets.UTF_8);
        String encodedTemplateObject = URLEncoder.encode(templateObject.toString(), StandardCharsets.UTF_8);

        // Form 데이터를 생성합니다.
        String formData = "receiver_uuids=" + encodedReceiverUuids + "&template_object=" + encodedTemplateObject;

        System.out.println("formData = " + formData);

        HttpEntity<String> request = new HttpEntity<>(formData, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        System.out.println("response.getBody() = " + response.getBody());

        if (response.getStatusCode() != HttpStatus.OK) {
            return false;
        }
        return true;
    }
}
