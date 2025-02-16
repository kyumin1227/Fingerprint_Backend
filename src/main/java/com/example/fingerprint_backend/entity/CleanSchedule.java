package com.example.fingerprint_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@Getter
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

    public CleanGroup setCleanGroup(CleanGroup cleanGroup) {
        CleanGroup previousCleanGroup = this.cleanGroup;
        this.cleanGroup = cleanGroup;
        return previousCleanGroup;
    }

    public void setCanceled(boolean canceled) {
        this.isCanceled = canceled;
    }

    /**
     * TODO : 스케줄을 이용하여 날짜가 하루 지날 경우 자동으로 호출 하도록 구현
     */
    public void updateGroupCleaningCount() {
        if (this.isCanceled) {
            return;
        }
        if (this.cleanGroup == null) {
            return;
        }
        this.cleanGroup.setCleaned(true);
    }

}
