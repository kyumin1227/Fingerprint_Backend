package com.example.fingerprint_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Entity
@NoArgsConstructor
@Getter @Setter
public class CleanGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne  @JoinColumn(nullable = false)
    private SchoolClass schoolClass;
    @Column(nullable = false)
    private int memberCount;  // 그룹의 최대 인원
    @OneToMany  @JoinColumn(nullable = false)
    private Set<CleanMember> members;
    private boolean isCleaned = false;  // 청소 완료 여부


    public CleanGroup(SchoolClass schoolClass, int memberCount, Set<CleanMember> members) {
        if (memberCount <= 0) {
            throw new IllegalArgumentException("그룹의 최대 인원은 0보다 커야 합니다.");
        }
        if (memberCount > 9) {
            throw new IllegalArgumentException("그룹의 최대 인원은 9명을 초과할 수 없습니다.");
        }
        if (members.size() > memberCount) {
            throw new IllegalArgumentException("멤버 수가 그룹의 최대 인원을 초과합니다.");
        }
        this.schoolClass = schoolClass;
        this.memberCount = memberCount;
        this.members = members;
    }

    public void appendMember(CleanMember member) {
        if (members.size() >= memberCount) {
            throw new IllegalArgumentException("더 이상 그룹에 추가할 수 없습니다.");
        } else if (members.contains(member)) {
            throw new IllegalArgumentException("이미 그룹에 존재하는 멤버입니다.");
        }
        members.add(member);
    }

    public void removeMember(CleanMember member) {
        if (!members.contains(member)) {
            throw new IllegalArgumentException("그룹에 존재하지 않는 멤버입니다.");
        }
        members.remove(member);
    }

    public void setCleaned(boolean cleaned) {
        if (cleaned != isCleaned && cleaned) {
            members.forEach(member -> member.setCleaningCount(member.getCleaningCount() + 1));
        } else if (cleaned != isCleaned) {
            members.forEach(member -> member.setCleaningCount(member.getCleaningCount() - 1));
        }
        isCleaned = cleaned;
    }

    public void changeMemberCount(int memberCount) {
        if (memberCount <= 0) {
            throw new IllegalArgumentException("그룹의 최대 인원은 0보다 커야 합니다.");
        }
        if (memberCount > 9) {
            throw new IllegalArgumentException("그룹의 최대 인원은 9명을 초과할 수 없습니다.");
        }
        if (members.size() > memberCount) {
            throw new IllegalArgumentException("멤버 수가 그룹의 최대 인원을 초과합니다.");
        }
        this.memberCount = memberCount;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CleanGroup that = (CleanGroup) o;
        return memberCount == that.memberCount && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, memberCount);
    }
}
