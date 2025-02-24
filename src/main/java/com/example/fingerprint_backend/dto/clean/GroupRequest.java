package com.example.fingerprint_backend.dto.clean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupRequest {
    private String areaName;
    private String className;
    private String cycle;
    private Integer groupSize;
}
