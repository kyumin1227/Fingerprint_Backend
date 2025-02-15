package com.example.fingerprint_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public class CleanArea {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private SchoolClass schoolClass;
    @Column(unique = true)
    private String name;
    @ElementCollection(targetClass = DayOfWeek.class)
    @CollectionTable(name = "clean_area_days", joinColumns = @JoinColumn(name = "clean_area_id"))
    @Column(name = "day_of_week")
    @Enumerated(EnumType.STRING)
    private Set<DayOfWeek> days = new HashSet<>();
    private Integer cycle = 0;
    @OneToOne
    private CleanSchedule lastSchedule;
    @OneToMany(mappedBy = "cleanArea")
    private Set<CleanSchedule> schedules = new HashSet<>();
    @OneToMany(mappedBy = "cleanArea")
    private Set<CleanMember> members = new HashSet<>();



    public CleanArea(String name) {
        this.name = name;
    }

    public void appendClassroom(SchoolClass schoolClass) {
        classrooms.add(schoolClass);
    }

    public void removeClassroom(SchoolClass schoolClass) {
        if (classrooms.contains(schoolClass)) {
            classrooms.remove(schoolClass);
            schoolClass.removeArea(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CleanArea cleanArea = (CleanArea) o;
        return Objects.equals(id, cleanArea.id) && Objects.equals(name, cleanArea.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
