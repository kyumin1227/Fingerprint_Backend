package com.example.fingerprint_backend.domain.fingerprint.service.log;

import com.example.fingerprint_backend.domain.fingerprint.entity.ClassClosingTime;
import com.example.fingerprint_backend.domain.fingerprint.entity.LogEntity;
import com.example.fingerprint_backend.domain.fingerprint.exception.LogException;
import com.example.fingerprint_backend.entity.MemberEntity;
import com.example.fingerprint_backend.domain.fingerprint.repository.ClassClosingTimeRepository;
import com.example.fingerprint_backend.domain.fingerprint.repository.LogRepository;
import com.example.fingerprint_backend.service.Member.MemberQueryService;
import com.example.fingerprint_backend.service.Member.MemberValidator;
import com.example.fingerprint_backend.types.LogAction;
import com.example.fingerprint_backend.types.MemberRole;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 로그와 문 닫힘을 관리하는 서비스
 */
@Service
@Transactional
@RequiredArgsConstructor
public class LogService {

    private final LogRepository logRepository;
    private final MemberQueryService memberQueryService;

    /**
     * 지문 인식 시 로그 생성
     *
     * @param studentNumber 학번
     * @param action        로그 액션
     * @throws LogException 학번이 존재하지 않을 경우, 1분 이내 중복 로그 발생 시
     */
    public LogEntity createLog(String studentNumber, LogAction action) {

        memberQueryService.getMemberByStudentNumber(studentNumber);
        checkDuplicateLog(studentNumber, action, LocalDateTime.now());

        LogEntity logEntity = new LogEntity(
                studentNumber,
                LocalDateTime.now(),
                action
        );

        return logRepository.save(logEntity);
    }

    /**
     * 로그 중복 확인
     *
     * @param studentNumber 학번
     * @param action        로그 액션
     * @param eventTime     로그 발생 시간
     * @throws LogException 이미 등록된 로그일 경우
     */
    public void checkDuplicateLog(String studentNumber, LogAction action, LocalDateTime eventTime) {

        LocalDateTime checkTime = eventTime.minusMinutes(1);

        logRepository.findByStudentNumberAndActionAndEventTimeAfter(studentNumber, action, checkTime)
                .ifPresent(log -> {
                    throw new LogException("이미 등록된 로그입니다.");
                });
    }

    /**
     * 액션, 시작 시간, 종료 시간으로 로그 조회
     *
     * @param action    로그 액션
     * @param startTime 시작 시간
     * @param endTime   종료 시간
     * @return 해당 조건에 맞는 로그 리스트
     */
    public List<LogEntity> getLogsInRangeByAction(
            LogAction action,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        return logRepository.findByActionAndEventTimeBetween(action, startTime, endTime);
    }

    /**
     * 학생 번호, 액션, 시작 시간, 종료 시간으로 로그 조회
     *
     * @param studentNumber 학번
     * @param action        로그 액션
     * @param startTime     시작 시간
     * @param endTime       종료 시간
     * @return 해당 조건에 맞는 로그 리스트
     */
    public List<LogEntity> getLogsInRangeByStudentNumberAndAction(
            String studentNumber,
            LogAction action,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        return logRepository.findByStudentNumberAndActionAndEventTimeBetween(studentNumber, action, startTime, endTime);
    }

}
