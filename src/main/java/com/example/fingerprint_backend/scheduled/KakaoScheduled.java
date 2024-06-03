package com.example.fingerprint_backend.scheduled;

import com.example.fingerprint_backend.entity.DateEntity;
import com.example.fingerprint_backend.entity.KakaoEntity;
import com.example.fingerprint_backend.entity.MemberEntity;
import com.example.fingerprint_backend.repository.DateRepository;
import com.example.fingerprint_backend.repository.KakaoRepository;
import com.example.fingerprint_backend.repository.MemberRepository;
import com.example.fingerprint_backend.service.KakaoService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class KakaoScheduled {

    private final KakaoRepository kakaoRepository;
    private final KakaoService kakaoService;
    private final DateRepository dateRepository;

    @Value("${KAKAO_CLIENT_ID}")
    private String kakaoClientId;

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Scheduled(cron = "0 0 17 * * ?")
    public void sendKakaoSessionNotHoliday() {

        Optional<DateEntity> byId = dateRepository.findById(LocalDate.now());

        if (byId.isEmpty()) {
            return;
        }

        Boolean isHoliday = byId.get().getIsHoliday();

        if (isHoliday) {
            return;
        }

        Set<String> members = byId.get().getMembers();

        if (members.size() < 5) {
            return;
        }

        for (String stdNum : members) {

        }

    }

    @Scheduled(cron = "0 0 21 * * ?")


    /**
     * 매일 10시에 refreshToken을 사용해서 어드민의 accessToken 재발급
     */
    @Scheduled(cron = "0 0 10 * * ?")
    public void saveNewAdminAccessToken() {

        Optional<KakaoEntity> admin = kakaoRepository.findById("0");

        String refreshToken = admin.get().getRefreshToken();

        String accessToken = kakaoService.getAccessToken(refreshToken);

        admin.get().setAccessToken(accessToken);

        KakaoEntity saved = kakaoRepository.save(admin.get());

        if (saved.getAccessToken().equals(accessToken)) {
            kakaoService.sendKakaoMessage(accessToken, "토큰 갱신 성공", saved.getUuid());
        }
    }
}
