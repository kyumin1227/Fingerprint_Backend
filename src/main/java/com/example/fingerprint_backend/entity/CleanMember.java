package com.example.fingerprint_backend.entity;

import com.example.fingerprint_backend.types.CleanRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@NoArgsConstructor
@Getter
public class CleanMember {
    @Id
    private String studentNumber;
    @ManyToOne
    private SchoolClass schoolClass;
    @ManyToOne
    @JoinColumn
    private CleanArea cleanArea;
    @Column(nullable = false)
    private String name;
    @Enumerated(EnumType.STRING)
    private CleanRole cleanRole = CleanRole.MEMBER;
    @Setter
    private String profileImage;
    @Setter
    private Integer cleaningCount = 0;


    public CleanMember(String studentNumber, String name, SchoolClass schoolClass) {
        validateParameters(studentNumber, name, schoolClass);
        this.studentNumber = studentNumber;
        this.name = name;
        this.schoolClass = schoolClass;
        this.cleanArea = schoolClass.getDefaultArea();
    }

    public CleanMember(String studentNumber, String name, SchoolClass schoolClass, CleanRole cleanRole) {
        validateParameters(studentNumber, name, schoolClass);
        this.studentNumber = studentNumber;
        this.name = name;
        this.schoolClass = schoolClass;
        this.cleanRole = cleanRole;
        this.cleanArea = schoolClass.getDefaultArea();
    }

    public void setCleanRole(CleanRole cleanRole) {
        if (cleanRole == null) {
            throw new IllegalStateException("역할은 null일 수 없습니다.");
        }
        this.cleanRole = cleanRole;
//        if (cleanRole == CleanRole.MANAGER) {
//            schoolClass.setManager(this);
//        }
    }

    private void validateParameters(String studentNumber, String name, SchoolClass schoolClass) {
        if (studentNumber == null || studentNumber.isEmpty()) {
            throw new IllegalStateException("학번은 null일 수 없습니다.");
        }
        if (schoolClass == null) {
            throw new IllegalStateException("반은 null일 수 없습니다.");
        }
        if (name == null || name.isEmpty()) {
            throw new IllegalStateException("이름은 null일 수 없습니다.");
        }
    }

    public void setCleanArea(CleanArea cleanArea) {
        this.cleanArea = cleanArea;
        if (cleanArea != null) {
            cleanArea.appendMember(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CleanMember that = (CleanMember) o;
        return Objects.equals(studentNumber, that.studentNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentNumber);
    }
}
