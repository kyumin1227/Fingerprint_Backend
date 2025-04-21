package com.example.fingerprint_backend.domain.fingerprint.entity;

import java.time.LocalDate;
import java.time.LocalTime;

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
}