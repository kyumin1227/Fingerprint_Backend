package com.example.fingerprint_backend.domain.fingerprint.entity;

import com.example.fingerprint_backend.domain.fingerprint.exception.CycleException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
public class AttendanceCycle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String studentNumber;
    private LocalDateTime attendTime;
    private LocalDateTime leaveTime;

    @OneToMany(mappedBy = "attendanceCycle", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OutingCycle> outingCycles = new ArrayList<>();

    public AttendanceCycle(String studentNumber, LocalDateTime attendTime) {
        this.studentNumber = studentNumber;
        this.attendTime = attendTime;
    }

    /**
     * 하교 시간 설정
     *
     * @param leaveTime 하교 시간
     * @throws CycleException 하교 시간이 등교 시간보다 이른 경우
     */
    public void setLeaveTime(LocalDateTime leaveTime) {
        if (leaveTime.isBefore(attendTime)) {
            throw new CycleException("등교 보다 이른 하교 시간입니다.");
        }
        this.leaveTime = leaveTime;
    }

    /**
     * 외출 기록 추가
     *
     * @param outingCycle 외출 기록
     */
    public void addOutingCycle(OutingCycle outingCycle) {
        if (outingCycle.getOutingStartTime().isBefore(attendTime)) {
            throw new CycleException("등교 시간보다 이른 외출입니다.");
        }
        if (outingCycle.getOutingEndTime() != null && outingCycle.getOutingEndTime().isAfter(leaveTime)) {
            throw new CycleException("하교 시간보다 늦은 외출입니다.");
        }
        if (!outingCycle.getStudentNumber().equals(this.studentNumber)) {
            throw new CycleException("학생 번호가 일치하지 않습니다.");
        }
        this.outingCycles.add(outingCycle);
        outingCycle.setAttendanceCycle(this);
    }
}
