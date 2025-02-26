package com.example.fingerprint_backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@NoArgsConstructor
@Getter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CleanMember {
    @Id
    private String studentNumber;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_class_id")
    @JsonBackReference
    private SchoolClass schoolClass;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    @JsonBackReference
    private CleanArea cleanArea;
    @Column(nullable = false)
    private String givenName;
    @Column(nullable = false)
    private String familyName;
    @Setter
    private String profileImage;
    @Setter
    private Integer cleaningCount = 0;


    public CleanMember(String studentNumber, String givenName, String familyName, SchoolClass schoolClass) {
        validateParameters(studentNumber, givenName, schoolClass);
        this.studentNumber = studentNumber;
        this.givenName = givenName;
        this.familyName = familyName;
        this.schoolClass = schoolClass;
        this.cleanArea = schoolClass.getDefaultArea();
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
        if (this.cleanArea != null) {
            this.cleanArea.removeMember(this);
        }
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
