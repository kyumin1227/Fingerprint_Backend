package com.example.fingerprint_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class CleanCountPerArea {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String studentNumber;
    @Column(nullable = false)
    private Long cleanAreaId;
    private int cleanCount = 0; // 청소 횟수

    public CleanCountPerArea(String studentNumber, Long cleanAreaId) {
        this.studentNumber = studentNumber;
        this.cleanAreaId = cleanAreaId;
    }

    public void increment() {
        this.cleanCount++;
    }

    public void decrement() {
        if (this.cleanCount > 0) {
            this.cleanCount--;
        }
    }
}
