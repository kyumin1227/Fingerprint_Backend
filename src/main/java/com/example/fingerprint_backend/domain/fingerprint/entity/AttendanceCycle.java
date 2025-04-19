package com.example.fingerprint_backend.domain.fingerprint.entity;

import com.example.fingerprint_backend.entity.LogEntity;
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

    @OneToMany
    @JoinTable(name = "attendance_log",
            joinColumns = @JoinColumn(name = "attendance_cycle_id"),
            inverseJoinColumns = @JoinColumn(name = "log_id"))
    private List<LogEntity> logEntities = new ArrayList<>();

    public AttendanceCycle(String studentNumber, LocalDateTime attendTime) {
        this.studentNumber = studentNumber;
        this.attendTime = attendTime;
    }

    /**
     * 하교 시간 설정
     *
     * @param leaveTime 하교 시간
     * @throws IllegalArgumentException 하교 시간이 등교 시간보다 이른 경우
     */
    public void setLeaveTime(LocalDateTime leaveTime) {
        if (leaveTime.isBefore(attendTime)) {
            throw new IllegalArgumentException("등교 보다 이른 하교 시간입니다.");
        }
        this.leaveTime = leaveTime;
    }

    /**
     * 출결, 외출 기록 추가
     *
     * @param logEntity 출결, 외출 기록
     */
    public void addLogEntity(LogEntity logEntity) {
        this.logEntities.add(logEntity);
    }
}
