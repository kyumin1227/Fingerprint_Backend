package com.example.fingerprint_backend.dto.clean;

import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class AreaRequest {
    private String areaName;
    private Set<DayOfWeek> daysOfWeek = new HashSet<>();
    private Integer cycle;
    private Boolean isDefault;
    private LocalDate startDate;
    private Integer display;
    private Integer groupSize;

    public void setDaysOfWeek(List<String> daysOfWeek) {
        Set<DayOfWeek> days = new HashSet<>();
        try {
            daysOfWeek.stream()
                    .map(day -> DayOfWeek.valueOf(day.toUpperCase()))
                    .forEach(days::add
            );
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("존재하지 않는 요일입니다.");
        }
        this.daysOfWeek = days;
    }
}
