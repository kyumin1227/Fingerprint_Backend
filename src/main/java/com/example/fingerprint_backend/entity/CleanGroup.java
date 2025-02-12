package com.example.fingerprint_backend.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class CleanGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToMany
    private List<CleanMember> members;
}
