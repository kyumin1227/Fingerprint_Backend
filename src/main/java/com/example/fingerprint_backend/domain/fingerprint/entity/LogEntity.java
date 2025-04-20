package com.example.fingerprint_backend.domain.fingerprint.entity;

import com.example.fingerprint_backend.types.LogAction;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter @Getter
@NoArgsConstructor
public class LogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String studentNumber;
    private LocalDateTime eventTime;
    @Enumerated(EnumType.STRING)
    private LogAction action;

    public LogEntity(String studentNumber, LocalDateTime eventTime, LogAction action) {
        this.studentNumber = studentNumber;
        this.eventTime = eventTime;
        this.action = action;
    }
}
