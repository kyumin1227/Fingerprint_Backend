package com.example.fingerprint_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
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
    private int memberCount;
    @OneToMany
    private Set<CleanMember> members = new HashSet<>(memberCount);
    private boolean isCleaned = false;  // 청소 완료 여부


    public CleanGroup(int memberCount) {
        this.memberCount = memberCount;
    }

    public CleanGroup(int memberCount, Set<CleanMember> members) {
        if (members.size() > memberCount) {
            throw new IllegalArgumentException("멤버 수가 그룹의 최대 인원을 초과합니다.");
        }
        this.memberCount = memberCount;
        this.members = members;
    }

    public void setMembers(Set<CleanMember> members) {
        if (members.size() > memberCount) {
            throw new IllegalArgumentException("멤버 수가 그룹의 최대 인원을 초과합니다.");
        }
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
