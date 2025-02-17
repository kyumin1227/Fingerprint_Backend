package com.example.fingerprint_backend.entity;

import com.example.fingerprint_backend.types.CleanRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Entity
@NoArgsConstructor
@Getter
public class SchoolClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String name;
    @OneToOne
    @JoinColumn(name = "manager_id")
    private CleanMember manager = null;
    @OneToOne
    @Setter
    private CleanArea defaultArea;
    @OneToMany(mappedBy = "schoolClass")
    private final List<CleanMember> classMembers = new ArrayList<>();
    @OneToMany(mappedBy = "schoolClass")
    private final List<CleanSchedule> schedules = new ArrayList<>();
    @OneToMany(mappedBy = "schoolClass")
    private final List<CleanArea> areas = new ArrayList<>();


    public SchoolClass(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalStateException("반 이름은 null일 수 없습니다.");
        }
        this.name = name;
    }

    public void setManager(CleanMember manager) {
        if (manager == null) {
            throw new IllegalStateException("관리자는 null일 수 없습니다.");
        }
        if (this.manager != null) {
            this.manager.setCleanRole(CleanRole.MEMBER);
        }
        this.manager = manager;
    }

    public void appendMember(CleanMember cleanMember) {
        classMembers.add(cleanMember);
    }

    public void removeMember(CleanMember cleanMember) {
        classMembers.remove(cleanMember);
    }

    public void appendSchedule(CleanSchedule cleanSchedule) {
        schedules.add(cleanSchedule);
    }

    public void removeSchedule(CleanSchedule cleanSchedule) {
        schedules.remove(cleanSchedule);
    }

    public void appendArea(CleanArea cleanArea) {
        areas.add(cleanArea);
    }

    public void removeArea(CleanArea cleanArea) {
        areas.remove(cleanArea);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SchoolClass schoolClass = (SchoolClass) o;
        return Objects.equals(id, schoolClass.id) && Objects.equals(name, schoolClass.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
