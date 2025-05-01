package com.example.fingerprint_backend.domain.fingerprint.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
public class ClassClosingTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime closingTime;
    private Long schoolClassId;
    private String closingMember;

    public ClassClosingTime(LocalDateTime closingTime, Long schoolClassId, String closingMember) {
        this.closingTime = closingTime;
        this.schoolClassId = schoolClassId;
        this.closingMember = closingMember;

    }
}
