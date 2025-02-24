package com.example.fingerprint_backend.dto.clean;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ScheduleRequest {
    private String areaName;
    private String className;
    private LocalDate date;
}
