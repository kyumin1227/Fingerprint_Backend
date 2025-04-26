package com.example.fingerprint_backend.domain.fingerprint.entity;

import java.time.LocalDate;

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
    private LocalDate effectiveDate;

    @Column(nullable = false)
    protected Long stayDuration = 0L;

    @Column(nullable = false)
    protected Long outDuration = 0L;

    protected BaseStats(String studentNumber, LocalDate effectiveDate) {
        this.studentNumber = studentNumber;
        this.effectiveDate = effectiveDate;
    }

    /**
     * 통계의 마지막 날짜를 반환합니다.
     *
     * @return 마지막 날짜
     */
    abstract public LocalDate getEndDate();
}