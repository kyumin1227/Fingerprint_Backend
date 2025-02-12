package com.example.fingerprint_backend.entity;

import com.example.fingerprint_backend.types.CleanAttendanceStatus;
import com.example.fingerprint_backend.types.CleanRole;
import jakarta.persistence.*;

@Entity
public class CleanMember {

    @Id
    private String studentNumber;
    private String name;
    @ManyToOne
    private Classroom classroom;
    @Enumerated(EnumType.STRING)
    private CleanAttendanceStatus cleanAttendanceStatus;
    @Enumerated(EnumType.STRING)
    private CleanRole cleanRole;
}
