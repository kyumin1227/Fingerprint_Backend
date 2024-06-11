package com.example.fingerprint_backend.scheduled;

import com.example.fingerprint_backend.entity.DateEntity;
import com.example.fingerprint_backend.entity.KakaoEntity;
import com.example.fingerprint_backend.entity.KeyEntity;
import com.example.fingerprint_backend.repository.DateRepository;
import com.example.fingerprint_backend.repository.KakaoRepository;
import com.example.fingerprint_backend.repository.KeyRepository;
import com.example.fingerprint_backend.service.KakaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class KakaoScheduled {

    private final KakaoRepository kakaoRepository;
    private final KakaoService kakaoService;
    private final DateRepository dateRepository;
    private final KeyRepository keyRepository;

    @Value("${KAKAO_CLIENT_ID}")
    private String kakaoClientId;

    private static String accessToken = "";

    /**
     * 평일의 신청 내역을 확인하여 결과를 카톡으로 송신
     * 17시 실행
     */
    @Scheduled(cron = "00 00 17 * * ?")
    @Transactional
    public void sendKakaoSessionNotHoliday() {

        System.out.println("accessToken holiday = " + accessToken);

//        accessToken 확인
        if (accessToken.equals("")) {
            saveNewAdminAccessToken();
        }

        Optional<DateEntity> dateInfo = dateRepository.findById(LocalDate.now());
        Optional<KeyEntity> keyInfo = keyRepository.findById(LocalDate.now());

        if (dateInfo.isEmpty()) {
            return;
        }

        Boolean isHoliday = keyInfo.isEmpty() ? false : keyInfo.get().getIsHoliday();

        if (isHoliday) {
            return;
        }

        Set<String> members = dateInfo.get().getMembers();

        if (members.size() < 5) {
            for (String stdNum : members) {
                Optional<KakaoEntity> targetStudentKakao = kakaoRepository.findById(stdNum);

                if (targetStudentKakao.isEmpty()) {
                    continue;
                }

                kakaoService.sendKakaoMessage(accessToken, "인원 부족으로 " + LocalDate.now() + "일 자습 연장이 취소되었습니다.", targetStudentKakao.get().getUuid());
            }
            return;
        }

        if (!dateInfo.get().getIsAble()  || keyInfo.get().getKeyStudent().equals("") || keyInfo.get().getSubManager().equals("")) {
            for (String stdNum : members) {
                Optional<KakaoEntity> targetStudentKakao = kakaoRepository.findById(stdNum);

                if (targetStudentKakao.isEmpty()) {
                    continue;
                }

                kakaoService.sendKakaoMessage(accessToken, "열쇠 담당자 부재로 " + LocalDate.now() + "일 자습 연장이 취소되었습니다.", targetStudentKakao.get().getUuid());
            }
            return;
        }

        for (String stdNum : members) {
            Optional<KakaoEntity> targetStudentKakao = kakaoRepository.findById(stdNum);

            if (targetStudentKakao.isEmpty() || targetStudentKakao.get().getUuid() == null || targetStudentKakao.get().getUuid().isEmpty()) {
                continue;
            }

            kakaoService.sendKakaoMessage(accessToken, LocalDate.now() + "일 자습이 연장되었습니다.", targetStudentKakao.get().getUuid());
        }

        kakaoService.sendKakaoMessage(accessToken, "열쇠를 수령해주세요", keyInfo.get().getKeyStudent());

    }

    /**
     * 휴일의 신청 내역을 확인하여 결과를 카톡으로 송신
     * 전날 17시 실행
     */
    @Scheduled(cron = "0 0 17 * * ?")
    @Transactional
    public void sendKakaoSessionHoliday() {

        //        accessToken 확인
        if (accessToken.equals("")) {
            saveNewAdminAccessToken();
        }

        Optional<DateEntity> dateInfo = dateRepository.findById(LocalDate.now().plusDays(1));
        Optional<KeyEntity> keyInfo = keyRepository.findById(LocalDate.now().plusDays(1));

        if (dateInfo.isEmpty()) {
            return;
        }

        Boolean isHoliday = keyInfo.isEmpty() ? false : keyInfo.get().getIsHoliday();

        if (!isHoliday) {
            return;
        }

        Set<String> members = dateInfo.get().getMembers();

        if (members.size() < 5) {
            for (String stdNum : members) {
                Optional<KakaoEntity> targetStudentKakao = kakaoRepository.findById(stdNum);

                if (targetStudentKakao.isEmpty()) {
                    continue;
                }

                kakaoService.sendKakaoMessage(accessToken, "인원 부족으로 " + LocalDate.now() + "일 교실 오픈이 취소되었습니다.", targetStudentKakao.get().getUuid());
            }
            return;
        }

        if (!dateInfo.get().getIsAble() || keyInfo.get().getKeyStudent() == null || keyInfo.get().getSubManager() == null) {
            for (String stdNum : members) {
                Optional<KakaoEntity> targetStudentKakao = kakaoRepository.findById(stdNum);

                if (targetStudentKakao.isEmpty()) {
                    continue;
                }

                kakaoService.sendKakaoMessage(accessToken, "담당자 부재로 " + LocalDate.now() + "일 교실 오픈이 취소되었습니다.", targetStudentKakao.get().getUuid());
            }
            return;
        }

        for (String stdNum : members) {
            Optional<KakaoEntity> targetStudentKakao = kakaoRepository.findById(stdNum);

            if (targetStudentKakao.isEmpty()) {
                continue;
            }

            kakaoService.sendKakaoMessage(accessToken, LocalDate.now() + "일\n" + keyInfo.get().getStartTime() + "시 부터 " + keyInfo.get().getEndTime() + "시 까지 교실 오픈합니다.", targetStudentKakao.get().getUuid());
        }

        Optional<KakaoEntity> targetStudentKakao = kakaoRepository.findById(keyInfo.get().getKeyStudent());

        kakaoService.sendKakaoMessage(accessToken, "열쇠를 수령해주세요", targetStudentKakao.get().getUuid());

    }

//    @Scheduled(cron = "0 35 18 * * ?")
    public void checkScheduled() {
        LocalDateTime now = LocalDateTime.now();
        System.out.println("스케줄 작동 = " + now);
    }

    /**
     * 매일 10시에 refreshToken을 사용해서 어드민의 accessToken 재발급
     */
    @Scheduled(cron = "0 0 10 * * ?")
    public void saveNewAdminAccessToken() {

        System.out.println("accessToken origin = " + accessToken);

        Optional<KakaoEntity> admin = kakaoRepository.findById("0");

        String refreshToken = admin.get().getRefreshToken();

        accessToken = kakaoService.getAccessToken(refreshToken);

        admin.get().setAccessToken(accessToken);

        KakaoEntity saved = kakaoRepository.save(admin.get());

        if (saved.getAccessToken().equals(accessToken)) {
            kakaoService.sendKakaoMessage(accessToken, "토큰 갱신 성공\n\n" + LocalDateTime.now(), saved.getUuid());
        }

        System.out.println("accessToken new = " + accessToken);
    }
}
