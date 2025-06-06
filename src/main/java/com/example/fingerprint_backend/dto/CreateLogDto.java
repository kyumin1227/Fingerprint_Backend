package com.example.fingerprint_backend.dto;

import com.example.fingerprint_backend.domain.fingerprint.types.LogAction;
import lombok.Getter;

@Getter
public class CreateLogDto {

    private String std_num;
    private LogAction action;
}
