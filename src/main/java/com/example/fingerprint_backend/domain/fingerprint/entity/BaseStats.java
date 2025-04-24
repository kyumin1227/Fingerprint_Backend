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

    @Column(nullable = false)
    private Integer totalAttendCount = 0;

    protected BaseStats(String studentNumber, LocalDate startDate) {
        this.studentNumber = studentNumber;
        this.startDate = startDate;
    }

    /**
     * 총 체류 시간 재설정
     *
     * @param stayDuration 총 체류 시간 (밀리초 단위)
     */
    public void setTotalStayDuration(Long stayDuration) {
        if (stayDuration < 0) {
            throw new StatsException("체류 시간은 음수일 수 없습니다.");
        }
        this.totalStayDuration = stayDuration;
    }

    /**
     * 총 외출 시간 재설정
     *
     * @param outDuration 총 외출 시간 (밀리초 단위)
     */
    public void setTotalOutDuration(Long outDuration) {
        if (outDuration < 0) {
            throw new StatsException("외출 시간은 음수일 수 없습니다.");
        }
        this.totalOutDuration = outDuration;
    }

    /**
     * 출석 횟수 재설정
     *
     * @param attendCount 출석 횟수
     */
    public void setTotalAttendCount(Integer attendCount) {
        if (attendCount < 0) {
            throw new StatsException("출석 횟수는 음수일 수 없습니다.");
        }
        this.totalAttendCount = attendCount;
    }

    /**
     * 평균 출석 시간 재설정
     *
     * @param avgAttendTime 평균 출석 시간
     */
    public void setAvgAttendTime(LocalTime avgAttendTime) {
        if (avgAttendTime == null) {
            throw new StatsException("평균 출석 시간은 null일 수 없습니다.");
        }
        this.avgAttendTime = avgAttendTime;
    }

    /**
     * 평균 퇴실 시간 재설정
     *
     * @param avgLeaveTime 평균 퇴실 시간
     */
    public void setAvgLeaveTime(LocalTime avgLeaveTime) {
        if (avgLeaveTime == null) {
            throw new StatsException("평균 퇴실 시간은 null일 수 없습니다.");
        }
        this.avgLeaveTime = avgLeaveTime;
    }
}