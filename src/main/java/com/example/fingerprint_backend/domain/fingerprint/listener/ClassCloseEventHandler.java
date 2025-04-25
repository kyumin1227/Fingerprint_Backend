package com.example.fingerprint_backend.domain.fingerprint.listener;

import com.example.fingerprint_backend.domain.fingerprint.entity.ClassClosingTime;
import com.example.fingerprint_backend.domain.fingerprint.event.ClassCloseEvent;
import com.example.fingerprint_backend.entity.MemberEntity;
import com.example.fingerprint_backend.service.Member.MemberQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ClassCloseEventHandler {

    private final MemberQueryService memberQueryService;

    public void handleClassCloseEvent(ClassCloseEvent event) {

        ClassClosingTime classClosingTime = event.classClosingTime();

        MemberEntity member = memberQueryService.getMemberByStudentNumber(classClosingTime.getClosingMember());
        Long id = member.getSchoolClass().getId();


    }
}
