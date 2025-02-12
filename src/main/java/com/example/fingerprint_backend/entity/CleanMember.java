package com.example.fingerprint_backend.entity;

import com.example.fingerprint_backend.types.CleanAttendanceStatus;
import com.example.fingerprint_backend.types.CleanRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@NoArgsConstructor
@AllArgsConstructor
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CleanMember that = (CleanMember) o;
        return Objects.equals(studentNumber, that.studentNumber) && Objects.equals(name, that.name) && Objects.equals(classroom, that.classroom) && cleanAttendanceStatus == that.cleanAttendanceStatus && cleanRole == that.cleanRole;
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentNumber, name, classroom, cleanAttendanceStatus, cleanRole);
    }
}
