package com.example.fingerprint_backend.dto.clean;

import com.example.fingerprint_backend.entity.CleanMember;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class InfoResponse {

    private LocalDate date;
    private Long groupId;
    private List<CleanMember> members;
    private Integer membersCount;
    private String cleanArea;
    private String className;
    private Boolean isCanceled;

    public InfoResponse(LocalDate date) {
        this.date = date;
        this.isCanceled = true;
    }

}
