package com.example.fingerprint_backend.entity;

import com.example.fingerprint_backend.types.MemberLanguage;
import com.example.fingerprint_backend.types.MemberRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class MemberEntity {
    @Id
    private String studentNumber;
    @Column(unique = true)
    private String email;
    private String name;
    private String givenName;
    private String familyName;
    @Enumerated(EnumType.STRING)
    private MemberLanguage language;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "member_roles", joinColumns = @JoinColumn(name = "member_student_number"))
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private List<MemberRole> roles = new ArrayList<>();
    @ManyToOne(fetch = FetchType.LAZY)
    private SchoolClass schoolClass;
    private String profileImage;
    private LocalDateTime registerTime; // 가입 일자

    public MemberEntity(String studentNumber, String email, String name, String givenName, String familyName, MemberLanguage language, String profileImage) {
        this.studentNumber = studentNumber;
        this.email = email;
        this.name = name;
        this.givenName = givenName;
        this.familyName = familyName;
        this.language = language;
        this.profileImage = profileImage;
        this.registerTime = LocalDateTime.now();
    }

    public void addRole(MemberRole role) {
        this.roles.add(role);
    }

    public void removeRole(MemberRole role) {
        if (!this.roles.contains(role)) {
            throw new IllegalStateException("해당 권한이 존재하지 않습니다.");
        }
        this.roles.remove(role);
    }

}
