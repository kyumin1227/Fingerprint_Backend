package com.example.fingerprint_backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter @Setter
public class KeyInfoDto {

    private LocalDate date;
    private String keyStudent;
    private String subManager;
    private LocalTime startTime;
    private LocalTime endTime;
    private String amendStudentNumber;
    private Boolean isHoliday;

}
