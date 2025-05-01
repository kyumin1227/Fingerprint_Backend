package com.example.fingerprint_backend.domain.fingerprint.service.cycle;

import com.example.fingerprint_backend.domain.fingerprint.entity.AttendanceCycle;
import com.example.fingerprint_backend.domain.fingerprint.entity.OutingCycle;
import com.example.fingerprint_backend.domain.fingerprint.repository.AttendanceCycleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class AttendanceCycleCommandService {

    private final AttendanceCycleRepository attendanceCycleRepository;

    /**
     * 출석 사이클을 생성합니다.
     *
     * @param studentNumber 학번
     * @param attendTime    출석 시간
     */
    public AttendanceCycle createAttendanceCycle(String studentNumber, LocalDateTime attendTime) {

        AttendanceCycle attendanceCycle = new AttendanceCycle(studentNumber, attendTime);
        return attendanceCycleRepository.save(attendanceCycle);
    }

    /**
     * 출석 사이클에 외출 사이클을 추가합니다.
     *
     * @param attendanceCycle 출석 사이클
     * @param outingCycle     외출 사이클
     */
    public void addOutingCycle(AttendanceCycle attendanceCycle, OutingCycle outingCycle) {

        attendanceCycle.addOutingCycle(outingCycle);
    }

}
