package com.example.fingerprint_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.orm.jpa.hibernate.SpringJtaPlatform;

import java.time.LocalDateTime;

@Entity
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
public class FingerPrintEntity {
    @Id
    private String studentNumber;
    @Column(length = 1000)
    private String fingerPrintImage1;
    @Column(length = 1000)
    private String fingerPrintImage2;
    private LocalDateTime fingerPrintTime;  // 지문 등록 일자
    private String salt;
}
