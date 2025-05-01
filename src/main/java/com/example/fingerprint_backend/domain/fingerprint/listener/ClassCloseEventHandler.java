package com.example.fingerprint_backend.domain.fingerprint.listener;

import com.example.fingerprint_backend.domain.fingerprint.entity.ClassClosingTime;
import com.example.fingerprint_backend.domain.fingerprint.event.ClassCloseEvent;
import com.example.fingerprint_backend.domain.fingerprint.service.cycle.CycleApplicationService;
import com.example.fingerprint_backend.entity.MemberEntity;
import com.example.fingerprint_backend.entity.SchoolClass;
import com.example.fingerprint_backend.service.Member.MemberQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ClassCloseEventHandler {

    private final MemberQueryService memberQueryService;
    private final CycleApplicationService cycleApplicationService;

    @EventListener
    @Transactional
    public void handleClassCloseEvent(ClassCloseEvent event) {

        System.out.println("호출");

        ClassClosingTime classClosingTime = event.classClosingTime();

        MemberEntity member = memberQueryService.getMemberByStudentNumber(classClosingTime.getClosingMember());
        SchoolClass schoolClass = member.getSchoolClass();

        cycleApplicationService.classAllCycleByClass(schoolClass);


    }
}
