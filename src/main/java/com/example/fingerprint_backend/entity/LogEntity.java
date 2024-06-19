package com.example.fingerprint_backend.entity;

import com.example.fingerprint_backend.types.LogAction;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter @Getter
public class LogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String studentNumber;
    private LocalDateTime eventTime;
    @Enumerated(EnumType.STRING)
    private LogAction action;
}
