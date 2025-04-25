package com.example.fingerprint_backend.domain.fingerprint.service.classClosingTime;

import com.example.fingerprint_backend.domain.fingerprint.entity.ClassClosingTime;
import com.example.fingerprint_backend.domain.fingerprint.event.ClassCloseEvent;
import com.example.fingerprint_backend.entity.MemberEntity;
import com.example.fingerprint_backend.service.Member.MemberQueryService;
import com.example.fingerprint_backend.service.Member.MemberValidator;
import com.example.fingerprint_backend.types.MemberRole;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class ClassClosingTimeApplicationService {

    private final MemberValidator memberValidator;
    private final MemberQueryService memberQueryService;
    private final ClassClosingTimeCommandService classClosingTimeCommandService;
    private final ApplicationEventPublisher applicationEventPublisher;
    @Qualifier("taskScheduler")
    private final TaskScheduler taskScheduler;

    /**
     * 문 닫힘 시간 등록
     *
     * @param closingMember 문 닫힘 담당자 학번
     * @param closingTime   문 닫힘 시간
     * @return 문 닫힘 시간 등록된 객체
     * @throws IllegalArgumentException 열쇠 담당자가 아닐 경우, 학번이 존재하지 않을 경우
     * @throws IllegalStateException    5분 이내에 연속으로 문을 닫을 경우
     */
    public ClassClosingTime createClosingTime(LocalDateTime closingTime, String closingMember) {

        memberValidator.validateMemberInRole(closingMember, MemberRole.KEY);

        MemberEntity member = memberQueryService.getMemberByStudentNumber(closingMember);

        classClosingTimeCommandService.checkDuplicateClose(member.getSchoolClass().getId(), closingTime);

        ClassClosingTime closingTimeObject = classClosingTimeCommandService.createClosingTime(closingTime, member.getSchoolClass().getId(), closingMember);

        taskScheduler.schedule(
                () -> publishClassCloseEvent(closingTimeObject),
//                TODO - 실제 환경에서는 10분 후 이벤트 발행
                Instant.now().plusSeconds(10) // 10분 후에 이벤트 발행
        );

        return closingTimeObject;
    }

    private void publishClassCloseEvent(ClassClosingTime closingTime) {
        applicationEventPublisher.publishEvent(new ClassCloseEvent(closingTime));
    }

}
