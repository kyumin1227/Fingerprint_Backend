package com.example.fingerprint_backend.service;

import com.example.fingerprint_backend.dto.CreateFingerPrintDto;
import com.example.fingerprint_backend.dto.CreateLogDto;
import com.example.fingerprint_backend.entity.*;
import com.example.fingerprint_backend.repository.DateRepository;
import com.example.fingerprint_backend.repository.FingerPrintRepository;
import com.example.fingerprint_backend.repository.LogRepository;
import com.example.fingerprint_backend.repository.MemberRepository;
import com.example.fingerprint_backend.types.LogAction;
import com.google.api.client.util.DateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FingerPrintService {

    private final FingerPrintRepository fingerPrintRepository;
    private final LogRepository logRepository;
    private final MemberRepository memberRepository;
    private final KakaoService kakaoService;
    private final KeyService keyService;
    private final DateRepository dateRepository;

    /**
     * 학번의 가입 여부 확인
     * @param stdNum
     * @return
     */
    public Boolean isMemberExist(String stdNum) {

        Optional<MemberEntity> byStudentNumber = memberRepository.findByStudentNumber(stdNum);

        if (byStudentNumber.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 학번의 지문 정보 존재 여부 확인
     * @param stdNum
     * @return
     */
    public Boolean isFingerPrintExist(String stdNum) {

        Optional<FingerPrintEntity> byId = fingerPrintRepository.findById(stdNum);

        if (byId.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 지문 등록
     * @param createFingerPrintDto
     * @return
     */
    public FingerPrintEntity create(CreateFingerPrintDto createFingerPrintDto) {
        FingerPrintEntity fingerPrintEntity = new FingerPrintEntity(
                createFingerPrintDto.getStd_num(),
                createFingerPrintDto.getFingerprint1(),
                createFingerPrintDto.getFingerprint2(),
                LocalDateTime.now(),
                createFingerPrintDto.getSalt());

        FingerPrintEntity saved = fingerPrintRepository.save(fingerPrintEntity);

        return saved;
    }

    /**
     * 지문 삭제
     * @param stdNum
     * @return
     */
    public Boolean delete(String stdNum) {
        Optional<FingerPrintEntity> byId = fingerPrintRepository.findById(stdNum);

        if (byId.isEmpty()) {
            return false;
        }

        fingerPrintRepository.deleteById(stdNum);

        return true;
    }

    /**
     * 모든 지문 정보 반환 (라즈베리파이 최초 기동 시 사용)
     * @return
     */
    public List<FingerPrintEntity> getAllFingerprint() {

        List<FingerPrintEntity> all = fingerPrintRepository.findAll();

        return all;
    }

    /**
     * 지문 인식 시
     * @param createLogDto
     * @return
     */
    public LogEntity createLog(CreateLogDto createLogDto) {

        LogEntity logEntity = new LogEntity();
        logEntity.setAction(createLogDto.getAction());
        logEntity.setStudentNumber(createLogDto.getStd_num());
        logEntity.setEventTime(LocalDateTime.now());

        LogEntity saved = logRepository.save(logEntity);

        return saved;
    }

    /**
     * 지문을 인식하였을 때 사용자에게 카카오톡을 송신하는 메소드
     * @param studentNumber
     * @param action
     * @return
     */
    public Boolean sendMessage(String studentNumber, LogAction action) {

        String uuid = kakaoService.getUuid(studentNumber);

        if (uuid.isEmpty()) {
            return false;
        }

        String adminAccessToken = kakaoService.getAdminAccessToken();

        LocalDateTime dateTime = LocalDateTime.now();
        LocalDate localDate = LocalDate.now();

        KeyEntity keyInfo = keyService.getKeyInfo(localDate.toString());

//        열쇠 담당이 등교 한 경우 신청한 사람들에게 알림 발송
        if (keyInfo.getKeyStudent().equals(studentNumber) && action.equals(LogAction.등교)) {
            Optional<DateEntity> byId = dateRepository.findById(localDate);

            Set<String> members = byId.get().getMembers();

            for (String member : members) {
                String member_uuid = kakaoService.getUuid(member);

                if (member_uuid.isEmpty()) {
                    continue;
                }

                kakaoService.sendKakaoMessage(adminAccessToken, "교실 문 열었습니다.", member_uuid);
            }
        };

        if (action.equals(LogAction.등교)) {
            kakaoService.sendKakaoMessage(adminAccessToken, dateTime + "\n출석 처리 되었습니다.", uuid);
            return true;
        } else if (action.equals(LogAction.하교)) {
            kakaoService.sendKakaoMessage(adminAccessToken, dateTime + "\n하교 처리 되었습니다.", uuid);
            return true;
        }

        return false;

    }

}
