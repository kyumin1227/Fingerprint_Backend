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
public class Classroom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @OneToMany(mappedBy = "classroom")
    private List<CleanMember> members = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "classroom_cleanarea",
            joinColumns = @JoinColumn(name = "classroom_id"),
            inverseJoinColumns = @JoinColumn(name = "cleanarea_id")
    )
    private Set<CleanArea> areas = new HashSet<>();


    public Classroom(String name) {
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
        Classroom classroom = (Classroom) o;
        return Objects.equals(id, classroom.id) && Objects.equals(name, classroom.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
