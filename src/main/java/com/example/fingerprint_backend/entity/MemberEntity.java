package com.example.fingerprint_backend.entity;

import com.example.fingerprint_backend.types.MemberLanguage;
import com.example.fingerprint_backend.types.MemberRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public class MemberEntity {
    @Id
    private String studentNumber;

    private String name;
    @Column(unique = true)
    private String email;
    private String kakao;
    @Enumerated(EnumType.STRING)
    private MemberLanguage language;
    @Enumerated(EnumType.STRING)
    private MemberRole role;
    private LocalDateTime registerTime; // 가입 일자
}
