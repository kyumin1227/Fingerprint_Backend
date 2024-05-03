package com.example.fingerprint_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
public class DateEntity {

    @Id
    private LocalDate date;
    private Boolean isHoliday;
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<MemberEntity> members;
}
