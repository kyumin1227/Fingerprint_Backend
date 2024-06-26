package com.example.fingerprint_backend.entity;

import com.example.fingerprint_backend.types.MemberLanguage;
import com.example.fingerprint_backend.types.MemberRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
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
    private Boolean signKakao;
    private LocalDateTime registerTime; // 가입 일자
}
