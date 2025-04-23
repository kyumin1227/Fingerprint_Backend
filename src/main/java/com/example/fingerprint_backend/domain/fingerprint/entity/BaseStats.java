package com.example.fingerprint_backend.domain.fingerprint.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import com.example.fingerprint_backend.domain.fingerprint.exception.StatsException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@MappedSuperclass
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String studentNumber;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private Long totalStayDuration = 0L;

    @Column(nullable = false)
    private Long totalOutDuration = 0L;

    @Column(nullable = false)
    private LocalTime avgAttendTime = LocalTime.MIDNIGHT;

    @Column(nullable = false)
    private LocalTime avgLeaveTime = LocalTime.MIDNIGHT;

    protected BaseStats(String studentNumber, LocalDate startDate) {
        this.studentNumber = studentNumber;
        this.startDate = startDate;
    }

    /**
     * 체류 시간 업데이트
     *
     * @param stayDuration 추가할 체류 시간 (밀리초 단위)
     */
    public void updateTotalStayDuration(Long stayDuration) {
        if (stayDuration < 0) {
            throw new StatsException("체류 시간은 음수를 더할 수 없습니다.");
        }
        this.totalStayDuration += stayDuration;
    }

    /**
     * 외출 시간 업데이트
     *
     * @param outDuration 추가할 외출 시간 (밀리초 단위)
     */
    public void updateTotalOutDuration(Long outDuration) {
        if (outDuration < 0) {
            throw new StatsException("외출 시간은 음수를 더할 수 없습니다.");
        }
        this.totalOutDuration += outDuration;
    }
}