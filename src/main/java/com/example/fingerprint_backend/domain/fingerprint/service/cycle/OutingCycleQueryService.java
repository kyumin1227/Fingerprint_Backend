package com.example.fingerprint_backend.domain.fingerprint.service.cycle;

import com.example.fingerprint_backend.domain.fingerprint.entity.OutingCycle;
import com.example.fingerprint_backend.domain.fingerprint.repository.OutingCycleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OutingCycleQueryService {

    private final OutingCycleRepository outingCycleRepository;

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
