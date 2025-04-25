package com.example.fingerprint_backend.domain.fingerprint.service.cycle;

import com.example.fingerprint_backend.domain.fingerprint.entity.AttendanceCycle;
import com.example.fingerprint_backend.domain.fingerprint.entity.OutingCycle;
import com.example.fingerprint_backend.domain.fingerprint.repository.OutingCycleRepository;
import com.example.fingerprint_backend.domain.fingerprint.util.DatePolicy;
import com.example.fingerprint_backend.types.LogAction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OutingCycleCommandService {

    private final OutingCycleRepository outingCycleRepository;
    private final OutingCycleQueryService outingCycleQueryService;

    /**
     * 외출 사이클을 생성합니다.
     *
     * @param studentNumber   학번
     * @param outingStartTime 외출 시작 시간
     * @param reason          외출 사유
     * @return 외출 기록 (등교 사이클에 연결 필요)
     */
    public OutingCycle createOutingCycle(String studentNumber, LocalDateTime outingStartTime, LogAction reason) {

        OutingCycle latestOpenOutingCycle = outingCycleQueryService.getLatestOpenOutingCycle(studentNumber);

        if (latestOpenOutingCycle != null) {
            // 이전의 외출을 복귀하지 않았다면, 해당 외출 사이클의 종료 시간을 외출 시작 시간으로 설정합니다.
            latestOpenOutingCycle.setOutingEndTime(latestOpenOutingCycle.getOutingStartTime());
        }

        OutingCycle outingCycle = new OutingCycle(studentNumber, outingStartTime, reason);

        return outingCycleRepository.save(outingCycle);
    }

    /**
     * 외출 사이클을 종료합니다.
     *
     * @param outingCycle   외출 사이클
     * @param outingEndTime 외출 종료 시간
     * @return 외출 기록
     */
    public OutingCycle closeOutingCycle(OutingCycle outingCycle, LocalDateTime outingEndTime) {

        outingCycle.setOutingEndTime(outingEndTime);

        return outingCycle;
    }

    /**
     * 외출 사이클을 강제로 종료합니다. (외출 사이클의 종료 시간을 외출 시작 시간으로 설정합니다.)
     *
     * <p>외출 사이클이 이미 종료된 경우, 아무런 동작도 하지 않습니다.</p>
     *
     * @param outingCycle 외출 사이클
     * @return 외출 기록
     */
    public OutingCycle forceCloseOutingCycle(OutingCycle outingCycle) {

        if (outingCycle.getOutingEndTime() != null) {
            return outingCycle;
        }

        return closeOutingCycle(outingCycle, outingCycle.getOutingStartTime());
    }

    /**
     * 모든 외출 사이클들을 종료 하고 가장 늦은 외출 종료 시간을 반환합니다.
     *
     * @param outingCycleList 외출 사이클 리스트
     * @return LocalDateTime 가장 늦은 외출 종료 시간 (없는 경우 null)
     */
    public LocalDateTime forceCloseAllOutingCycles(List<OutingCycle> outingCycleList) {

        LocalDateTime localDateTime = null;

        for (OutingCycle outingCycle : outingCycleList) {
            OutingCycle closedOutingCycle = forceCloseOutingCycle(outingCycle);
            localDateTime = DatePolicy.max(localDateTime, closedOutingCycle.getOutingEndTime());
        }

        return localDateTime;
    }

}
