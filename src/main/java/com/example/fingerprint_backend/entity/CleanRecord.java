package com.example.fingerprint_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"schedule_id", "group_id"})
})
public class CleanRecord {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "schedule_id", nullable = false)
    private CleanSchedule cleanSchedule;
    @OneToOne
    @JoinColumn(name = "group_id", nullable = false)
    private CleanGroup cleanGroup;

    public CleanRecord(CleanSchedule cleanSchedule, CleanGroup cleanGroup) {
        this.cleanSchedule = cleanSchedule;
        this.cleanGroup = cleanGroup;
    }
}
