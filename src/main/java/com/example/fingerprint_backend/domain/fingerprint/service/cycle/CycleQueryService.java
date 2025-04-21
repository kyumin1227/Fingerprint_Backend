package com.example.fingerprint_backend.domain.fingerprint.service.cycle;

import com.example.fingerprint_backend.domain.fingerprint.entity.AttendanceCycle;
import com.example.fingerprint_backend.domain.fingerprint.entity.OutingCycle;
import com.example.fingerprint_backend.domain.fingerprint.repository.OutingCycleRepository;
import org.springframework.stereotype.Service;
import com.example.fingerprint_backend.domain.fingerprint.repository.AttendanceCycleRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CycleQueryService {

    private final AttendanceCycleRepository attendanceCycleRepository;
    private final OutingCycleRepository outingCycleRepository;

    public CycleQueryService(AttendanceCycleRepository attendanceCycleRepository, OutingCycleRepository outingCycleRepository) {
        this.attendanceCycleRepository = attendanceCycleRepository;
        this.outingCycleRepository = outingCycleRepository;
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

    /**
     * 학번으로 아직 복귀하지 않은 가장 최신의 외출 주기를 가져오는 메소드
     *
     * @param studentNumber 학번
     * @return OutingCycle
     */
    public OutingCycle getLatestOpenOutingCycle(String studentNumber) {
        return outingCycleRepository
                .findTopByStudentNumberAndOutingEndTimeIsNullOrderByOutingStartTimeDesc(studentNumber)
                .orElse(null);
    }

    /**
     * 학번으로 가장 최신의 외출 주기를 가져오는 메소드
     *
     * @param studentNumber 학번
     * @return OutingCycle
     */
    public OutingCycle getLatestOutingCycle(String studentNumber) {
        return outingCycleRepository
                .findTopByStudentNumberOrderByOutingStartTimeDesc(studentNumber)
                .orElse(null);
    }
}
