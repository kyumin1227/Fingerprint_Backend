package com.example.fingerprint_backend.domain.fingerprint.entity;

import com.example.fingerprint_backend.domain.fingerprint.exception.CycleException;
import com.example.fingerprint_backend.types.LogAction;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class OutingCycle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String studentNumber;

    private LocalDateTime outingStartTime;
    private LocalDateTime outingEndTime;
    private LogAction reason;

    @ManyToOne(fetch = FetchType.LAZY)
    @Setter(AccessLevel.PACKAGE)
    @JsonIgnore
    private AttendanceCycle attendanceCycle;

    public OutingCycle(String studentNumber, LocalDateTime outingStartTime, LogAction reason) {
        if (studentNumber == null || studentNumber.isEmpty()) {
            throw new CycleException("학생 번호는 비어 있을 수 없습니다.");
        }
        if (outingStartTime == null) {
            throw new CycleException("외출 시작 시간은 비어 있을 수 없습니다.");
        }
        if (reason == null) {
            throw new CycleException("외출 사유는 비어 있을 수 없습니다.");
        }
        this.studentNumber = studentNumber;
        this.outingStartTime = outingStartTime;
        this.reason = reason;
    }

    /**
     * 외출 종료 시간 설정
     *
     * @param outingEndTime 외출 종료 시간
     * @throws CycleException 외출 종료 시간이 외출 시작 시간보다 이른 경우, 이미 외출 종료 시간이 설정된 경우
     */
    public void setOutingEndTime(LocalDateTime outingEndTime) {
        if (outingEndTime.isBefore(outingStartTime)) {
            throw new CycleException("외출 종료 시간이 외출 시작 시간보다 빠를 수 없습니다.");
        }
        if (this.outingEndTime != null) {
            throw new CycleException("이미 외출 종료 시간이 설정되어 있습니다.");
        }
        this.outingEndTime = outingEndTime;
    }

    /**
     * 외출 사이클의 총 외출 시간을 계산합니다.
     *
     * @return 총 외출 시간 (밀리초 단위)
     */
    public Long getTotalOutingDuration() {
        if (outingEndTime == null) {
            return 0L;
        }
        return Duration.between(outingStartTime, outingEndTime).toMillis();
    }

}
