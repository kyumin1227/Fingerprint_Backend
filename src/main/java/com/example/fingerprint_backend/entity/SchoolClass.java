package com.example.fingerprint_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class SchoolClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    @OneToOne
    private CleanMember manager;
    @OneToMany(mappedBy = "schoolClass")
    private Set<CleanMember> members = new HashSet<>();
    @OneToMany(mappedBy = "schoolClass")
    private Set<CleanSchedule> schedules = new HashSet<>();
    @OneToMany(mappedBy = "schoolClass")
    private Set<CleanArea> areas = new HashSet<>();
    @OneToMany(mappedBy = "schoolClass")
    private Set<CleanGroup> groups = new HashSet<>();


    public SchoolClass(String name) {
        this.name = name;
    }

    public void appendMember(CleanMember cleanMember) {
        if (!members.contains(cleanMember)) {
            members.add(cleanMember);
        }
    }

    public void removeMember(CleanMember cleanMember) {
        members.remove(cleanMember);
    }

    public void appendArea(CleanArea cleanArea) {
        if (!areas.contains(cleanArea)) {
            areas.add(cleanArea);
            cleanArea.appendClassroom(this);
        }
    }

    public void removeArea(CleanArea cleanArea) {
        if (areas.contains(cleanArea)) {
            areas.remove(cleanArea);
            cleanArea.removeClassroom(this);
        }
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
