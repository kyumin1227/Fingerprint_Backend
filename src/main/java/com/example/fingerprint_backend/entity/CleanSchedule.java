package com.example.fingerprint_backend.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class CleanSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate date;
    private String cleanArea;
    @ManyToOne
    private Classroom classroom;
    @ManyToOne
    private CleanGroup cleanGroup;
}
