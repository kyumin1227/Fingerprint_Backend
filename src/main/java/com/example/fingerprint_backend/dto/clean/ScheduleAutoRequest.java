package com.example.fingerprint_backend.dto.clean;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class ScheduleAutoRequest {
    private String areaName;
    private String className;
    private List<String> daysOfWeek;
    private Integer cycle;
    private Integer count;
    private LocalDate date;
}
