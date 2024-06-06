package com.example.fingerprint_backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class KeyEntity {

    @Id
    private LocalDate date;
    private String keyStudent;
    private String subManager;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDateTime amendDate;
    private String amendStudentNumber;
    private Boolean isHoliday;
}
