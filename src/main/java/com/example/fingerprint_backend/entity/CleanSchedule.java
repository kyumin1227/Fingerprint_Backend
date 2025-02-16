package com.example.fingerprint_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@Getter @Setter
@Table(name = "clean_schedule",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"date", "clean_area_id", "school_class_id"})})
public class CleanSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne  @JoinColumn(nullable = false)
    private CleanArea cleanArea;
    @ManyToOne  @JoinColumn(nullable = false)
    private SchoolClass schoolClass;
    @OneToOne
    private CleanGroup cleanGroup;
    @Column(nullable = false)
    private LocalDate date;
    private boolean isCanceled = false; // 청소 취소 여부

    public CleanSchedule(LocalDate date, CleanArea cleanArea, SchoolClass schoolClass) {
        this.date = date;
        this.cleanArea = cleanArea;
        this.schoolClass = schoolClass;
    }

}
