package com.example.fingerprint_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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


    public void setMember(CleanMember cleanMember) {
        if (!members.contains(cleanMember)) {
            members.add(cleanMember);
        }
    }


    public void removeMember(CleanMember cleanMember) {
        members.remove(cleanMember);
    }
}
