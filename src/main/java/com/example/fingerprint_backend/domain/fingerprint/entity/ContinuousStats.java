package com.example.fingerprint_backend.domain.fingerprint.entity;

import com.example.fingerprint_backend.domain.fingerprint.exception.StatsException;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@MappedSuperclass
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class ContinuousStats extends BaseStats{

    @Column(nullable = false)
    private LocalTime avgAttendTime = LocalTime.MIDNIGHT;

    @Column(nullable = false)
    private LocalTime avgLeaveTime = LocalTime.MIDNIGHT;

    @Column(nullable = false)
    private Integer totalAttendCount = 0;

    protected ContinuousStats(String studentNumber, LocalDate effectiveDate) {
        super(studentNumber, effectiveDate);
    }

    /**
     * 총 체류 시간 재설정
     *
     * @param stayDuration 총 체류 시간 (밀리초 단위)
     */
    public void setStayDuration(Long stayDuration) {
        if (stayDuration < 0) {
            throw new StatsException("체류 시간은 음수일 수 없습니다.");
        }
        super.stayDuration = stayDuration;
    }

    /**
     * 총 외출 시간 재설정
     *
     * @param outDuration 총 외출 시간 (밀리초 단위)
     */
    public void setOutDuration(Long outDuration) {
        if (outDuration < 0) {
            throw new StatsException("외출 시간은 음수일 수 없습니다.");
        }
        super.outDuration = outDuration;
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
