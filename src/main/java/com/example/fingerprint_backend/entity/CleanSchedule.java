package com.example.fingerprint_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@Getter @Setter
public class CleanSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate date;
    @ManyToOne
    private CleanArea cleanArea;
    @ManyToOne
    private Classroom classroom;
    @ManyToOne
    private CleanGroup cleanGroup;
    private boolean isCanceled = false; // 청소 취소 여부

    public CleanSchedule(LocalDate date, CleanArea cleanArea, Classroom classroom) {
        this.date = date;
        this.cleanArea = cleanArea;
        this.classroom = classroom;
    }

}
