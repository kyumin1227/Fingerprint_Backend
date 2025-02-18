package com.example.fingerprint_backend.dto.clean;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AreaRequest {
    private String areaName;
    private String className;
    private List<String> daysOfWeek;
    private Integer cycle;
    private Boolean isDefault;
}
