package com.example.fingerprint_backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class FingerPrintEntity {
    @Id
    private Integer indexNum;
    private String fingerPrintImage1;
    private String fingerPrintImage2;
    private String studentNumber;
}
