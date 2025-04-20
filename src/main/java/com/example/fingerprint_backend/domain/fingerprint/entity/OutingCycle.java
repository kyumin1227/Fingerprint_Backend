package com.example.fingerprint_backend.domain.fingerprint.entity;

import com.example.fingerprint_backend.domain.fingerprint.exception.CycleException;
import com.example.fingerprint_backend.types.LogAction;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    public void setOutingEndTime(LocalDateTime outingEndTime) {
        if (outingEndTime.isBefore(outingStartTime)) {
            throw new CycleException("외출 종료 시간이 외출 시작 시간보다 이릅니다.");
        }
        this.outingEndTime = outingEndTime;
    }

}
