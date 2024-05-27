package com.example.fingerprint_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
@AllArgsConstructor
public class DateInfoDto {

    private LocalDate date;
    private Boolean sign;
    private Integer people;
    private Boolean isHoliday;
    private Boolean isAble;
}
