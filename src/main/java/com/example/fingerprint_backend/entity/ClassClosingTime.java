package com.example.fingerprint_backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

/**
 * 반의 인원이 등교 시 생성 되는 클래스
 */
@Entity
public class ClassClosingTime {
    @Id
    private Long id;
    private LocalDateTime closingTime;
    private Long schoolClassId;
    private String closingMember;
}
