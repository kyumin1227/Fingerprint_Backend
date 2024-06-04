package com.example.fingerprint_backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
public class KeyEntity {

    @Id
    private LocalDate date;
    private String studentNumber;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDateTime amendDate;
    private String amendStudentNumber;
}
