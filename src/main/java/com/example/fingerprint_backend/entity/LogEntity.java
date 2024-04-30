package com.example.fingerprint_backend.entity;

import com.example.fingerprint_backend.types.LogAction;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class LogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String studentNumber;
    private LocalDateTime eventTime;
    private LogAction action;
}
