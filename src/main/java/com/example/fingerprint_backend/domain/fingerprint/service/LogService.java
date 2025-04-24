package com.example.fingerprint_backend.domain.fingerprint.service;

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

/**
 * 로그와 문 닫힘을 관리하는 서비스
 */
@Service
@Transactional
@RequiredArgsConstructor
public class LogService {

    private final LogRepository logRepository;
    private final ClassClosingTimeRepository classClosingTimeRepository;
    private final MemberValidator memberValidator;
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

    /**
     * 문 닫힘 시간 등록
     *
     * @param closingMember 문 닫힘 담당자 학번
     * @param closingTime   문 닫힘 시간
     * @return 문 닫힘 시간 등록된 객체
     * @throws LogException          열쇠 담당자가 아닐 경우, 학번이 존재하지 않을 경우
     * @throws IllegalStateException 5분 이내에 문을 닫았을 경우
     */
    public ClassClosingTime createClosingTime(LocalDateTime closingTime, String closingMember) {

        try {
            memberValidator.validateMemberInRole(closingMember, MemberRole.KEY);
        } catch (LogException e) {
            throw new LogException("열쇠 담당자만 문을 닫을 수 있습니다.");
        }

        MemberEntity member = memberQueryService.getMemberByStudentNumber(closingMember);

        checkDuplicateClose(member.getSchoolClass().getId(), closingTime);

        ClassClosingTime classClosingTime = new ClassClosingTime(closingTime, member.getSchoolClass().getId(), member.getStudentNumber());

        return classClosingTimeRepository.save(classClosingTime);
    }

    /**
     * 문닫음 중복 확인
     *
     * @param classId     반 ID
     * @param closingTime 로그 발생 시간
     * @throws LogException 5분 이내에 문을 닫았을 경우
     */
    public void checkDuplicateClose(Long classId, LocalDateTime closingTime) {

        LocalDateTime checkTime = closingTime.minusMinutes(5);

        classClosingTimeRepository.findBySchoolClassIdAndClosingTimeAfter(classId, checkTime)
                .ifPresent(log -> {
                    throw new LogException("이미 문이 닫혀있습니다.");
                });
    }

    /**
     * 해당 시간 이후의 문 닫힘 시간을 반환하는 함수
     *
     * @param classId   반 ID
     * @param checkTime 확인할 시간
     * @return 해당 시간 이후의 문 닫힘 객체
     * @throws LogException 해당 시간 이후의 문 닫힘 시간이 없을 경우
     */
    public ClassClosingTime getClassClosingTimeByTimeAfter(Long classId, LocalDateTime checkTime) {
        return classClosingTimeRepository.findBySchoolClassIdAndClosingTimeAfter(classId, checkTime)
                .orElseThrow(() -> new LogException("해당 시간 이후의 문 닫힘 시간이 없습니다."));
    }

}
