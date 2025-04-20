package com.example.fingerprint_backend.domain.fingerprint.service;

import com.example.fingerprint_backend.domain.fingerprint.entity.AttendanceCycle;
import org.springframework.stereotype.Service;
import com.example.fingerprint_backend.domain.fingerprint.repository.AttendanceCycleRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CycleQueryService {

    private final AttendanceCycleRepository attendanceCycleRepository;

    public CycleQueryService(AttendanceCycleRepository attendanceCycleRepository) {
        this.attendanceCycleRepository = attendanceCycleRepository;
    }

    /**
     * 학번으로 아직 하교하지 않은 가장 최신의 출석 주기를 가져오는 메소드
     *
     * @param studentNumber 학번
     * @return AttendanceCycle
     */
    public AttendanceCycle getLatestOpenCycle(String studentNumber) {
        return attendanceCycleRepository
                .findTopByStudentNumberAndLeaveTimeIsNullOrderByAttendTimeDesc(studentNumber)
                .orElse(null);
    }

    /**
     * 학번으로 가장 최신의 출석 주기를 가져오는 메소드
     *
     * @param studentNumber 학번
     * @return AttendanceCycle
     */
    public AttendanceCycle getLatestCycle(String studentNumber) {
        return attendanceCycleRepository
                .findTopByStudentNumberOrderByAttendTimeDesc(studentNumber)
                .orElse(null);
    }

}
