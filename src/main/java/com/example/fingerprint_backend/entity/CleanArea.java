package com.example.fingerprint_backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

@Entity
@NoArgsConstructor
@Getter
public class CleanArea {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    @JsonBackReference
    private SchoolClass schoolClass;
    @Column(nullable = false)
    private String name;
    @ElementCollection(targetClass = DayOfWeek.class)
    @CollectionTable(name = "clean_area_days", joinColumns = @JoinColumn(name = "clean_area_id"))
    @Column(name = "day_of_week")
    @Enumerated(EnumType.STRING)
    private Set<DayOfWeek> days;
    private Integer cycle = 0;
    private LocalDate lastScheduledDate = LocalDate.now();
    @OneToMany(mappedBy = "cleanArea")
    @JsonManagedReference
    private List<CleanSchedule> schedules = new ArrayList<>();
    @OneToMany(mappedBy = "cleanArea")
    @JsonManagedReference
    private List<CleanMember> members = new ArrayList<>();
    @OneToMany(mappedBy = "cleanArea")
    @OrderBy("id")
    @JsonManagedReference
    private List<CleanGroup> groups = new ArrayList<>();


    public CleanArea(String name, SchoolClass schoolClass, Set<DayOfWeek> days, Integer cycle) {
        if (name == null || name.isEmpty()) {
            throw new IllegalStateException("청소 구역 이름은 null일 수 없습니다.");
        }
        if (schoolClass == null) {
            throw new IllegalStateException("반은 null일 수 없습니다.");
        }
        if (cycle < 0) {
            throw new IllegalStateException("주기는 0보다 작을 수 없습니다.");
        }
        this.name = name;
        this.schoolClass = schoolClass;
        if (days != null) {
            this.days = days;
        }
        this.cycle = cycle;

        schoolClass.appendArea(this);
    }

    public void appendMember(CleanMember member) {
        members.add(member);
    }

    public void removeMember(CleanMember member) {
        members.remove(member);
    }

    public void appendSchedule(CleanSchedule schedule) {
        schedules.add(schedule);
    }

    public void removeSchedule(CleanSchedule schedule) {
        schedules.remove(schedule);
    }

    public void appendGroup(CleanGroup group) {
        groups.add(group);
    }

    public void setCycle(Integer cycle) {
        if (cycle < 0) {
            throw new IllegalStateException("주기는 0보다 작을 수 없습니다.");
        }
        this.cycle = cycle;
    }

    public void setDays(Set<DayOfWeek> days) {
        if (days == null) {
            throw new IllegalStateException("요일은 null일 수 없습니다.");
        }
        this.days = days;
    }

    public void setLastScheduledDate(LocalDate lastScheduledDate) {
        if (lastScheduledDate.isBefore(LocalDate.now())) {
            throw new IllegalStateException("마지막 스케줄링 날짜는 과거 일수 없습니다.");
        }
        this.lastScheduledDate = lastScheduledDate;
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
