package com.example.fingerprint_backend.domain.fingerprint.service.cycle;

import com.example.fingerprint_backend.domain.fingerprint.entity.AttendanceCycle;
import com.example.fingerprint_backend.domain.fingerprint.repository.OutingCycleRepository;
import org.springframework.stereotype.Service;
import com.example.fingerprint_backend.domain.fingerprint.repository.AttendanceCycleRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AttendanceCycleQueryService {

    private final AttendanceCycleRepository attendanceCycleRepository;
    private final OutingCycleRepository outingCycleRepository;

    public AttendanceCycleQueryService(AttendanceCycleRepository attendanceCycleRepository, OutingCycleRepository outingCycleRepository) {
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
     * 학번 리스트로 아직 하교하지 않은 출석 주기를 가져오는 메소드
     *
     * @param studentNumberList 학번 리스트
     * @return List<AttendanceCycle>
     */
    public List<AttendanceCycle> getAllOpenCyclesByStudentNumber(List<String> studentNumberList) {

        return attendanceCycleRepository.findAllByStudentNumberInAndLeaveTimeIsNull(studentNumberList);
    }

}
