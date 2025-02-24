package com.example.fingerprint_backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

@Entity
@NoArgsConstructor
@Getter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
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
    private Integer display = 5;    // 한 번에 보여줄 스케줄 수
    private Integer groupSize = 4;  // 그룹의 최대 인원
    @OneToMany(mappedBy = "cleanArea")
    @JsonIgnore
    private List<CleanSchedule> schedules = new ArrayList<>();
    @OneToMany(mappedBy = "cleanArea")
    @JsonManagedReference
    @JsonIgnore
    private List<CleanMember> members = new ArrayList<>();
    @OneToMany(mappedBy = "cleanArea")
    @OrderBy("id")
    @JsonIgnore
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
            throw new IllegalStateException("스케줄 생성 날짜는 과거 일수 없습니다.");
        }
        this.lastScheduledDate = lastScheduledDate;
    }

    public void setDisplay(Integer display) {
        if (display < 0) {
            throw new IllegalStateException("한 번에 보여줄 스케줄 수는 0보다 작을 수 없습니다.");
        }
        this.display = display;
    }

    public void setGroupSize(Integer groupSize) {
        if (groupSize <= 0) {
            throw new IllegalStateException("그룹의 최대 인원은 0보다 커야 합니다.");
        }
        if (groupSize > 9) {
            throw new IllegalStateException("그룹의 최대 인원은 9명을 초과할 수 없습니다.");
        }
        this.groupSize = groupSize;
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
