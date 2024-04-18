package com.example.fingerprint_backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.List;

@Entity
public class MemberEntity {
    @Id
    private String studentNumber;

    private String name;
}
