package com.example.fingerprint_backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class FingerPrintEntity {
    @Id
    private Integer indexNum;
    private String fingerPrintImage1;
    private String fingerPrintImage2;
    private String studentNumber;
    private LocalDateTime fingerPrintTime;  // 지문 등록 일자
}
