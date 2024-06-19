package com.example.fingerprint_backend.service;

import com.example.fingerprint_backend.dto.KakaoDto;
import com.example.fingerprint_backend.entity.KakaoEntity;
import com.example.fingerprint_backend.repository.KakaoRepository;
import com.google.gson.JsonElement;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class KakaoService {

    private final KakaoRepository kakaoRepository;
    private final TaskScheduler taskScheduler;

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

        System.out.println("studentNumber = " + studentNumber);
        System.out.println("profileId = " + profileId);

        taskScheduler.schedule(() -> {
            System.out.println("call schedule");
            System.out.println("profileId = " + profileId);
            System.out.println("studentNumber = " + studentNumber);
            String uuid = getUuidFirst(studentNumber, profileId);
            System.out.println("call in uuid = " + uuid);

            // UUID를 업데이트하고 저장
            Optional<KakaoEntity> byId = kakaoRepository.findById(studentNumber);
            if (byId.isPresent()) {
                KakaoEntity kakaoEntity = byId.get();
                kakaoEntity.setUuid(uuid);
                kakaoRepository.save(kakaoEntity);
            }
        }, new Date(System.currentTimeMillis() + 5 * 60 * 1000));

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

    /**
     * 최초 등록 시 UUID를 가져오는 서비스
     * @param studentNumber
     * @param profile_id
     * @return
     */
    public String getUuidFirst(String studentNumber, String profile_id) {

        System.out.println("Call getUuid");

        String url = "https://kapi.kakao.com/v1/api/talk/friends?friend_order=favorite&limit=100&order=asc";

        String accessToken = kakaoRepository.findById("0").get().getAccessToken();

        System.out.println("accessToken = " + accessToken);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

        JSONObject jsonObject = new JSONObject(response.getBody());

        JSONArray elements = jsonObject.getJSONArray("elements");

        System.out.println("elements = " + elements);

        for (int i = 0; i < elements.length(); i++) {
            JSONObject element = elements.getJSONObject(i);
            System.out.println("element = " + element);

            // 친구의 UUID 추출 및 출력
            String id = element.get("id").toString();
            System.out.println("id = " + id);
            System.out.println("profile_id = " + profile_id);
            if (id.equals(profile_id)) {
                String uuid = element.getString("uuid");
                System.out.println("UUID: " + uuid);
                sendKakaoMessage(accessToken, "카카오톡 세팅이 완료되었습니다.", uuid);
                return uuid;
            }
        }

        return "";

    }

    /**
     * 학생의 uuid를 가져오는 메소드
     * @param studentNumber
     * @return uuid
     */
    public String getUuid(String studentNumber) {

        Optional<KakaoEntity> byId = kakaoRepository.findById(studentNumber);

        if (byId.isEmpty()) {
            return "";
        }

        return byId.get().getUuid();
    }

    /**
     * 관리자의 엑세스 토큰 가져오는 메소드
     * @return accesstoken(Admin)
     */
    public String getAdminAccessToken() {

        String accessToken = kakaoRepository.findById("0").get().getAccessToken();

        return accessToken;
    }
}
