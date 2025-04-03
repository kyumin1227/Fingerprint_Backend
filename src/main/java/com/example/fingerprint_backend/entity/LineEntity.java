package com.example.fingerprint_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
public class LineEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "student_number")
    @Getter
    private MemberEntity member;
    @Column(unique = true, nullable = false)
    @Getter
    private String lineId;
    private Boolean receiveLineMessage; // 라인 메시지 수신 여부
    private LocalDateTime createTime; // 생성 일자
    private LocalDateTime updateTime; // 수신 여부 수정 일자

    public LineEntity(MemberEntity member, String lineId) {
        this.member = member;
        this.lineId = lineId;
        this.receiveLineMessage = true;
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    public void updateReceiveLineMessage(Boolean receiveLineMessage) {
        this.receiveLineMessage = receiveLineMessage;
        this.updateTime = LocalDateTime.now();
    }
}
