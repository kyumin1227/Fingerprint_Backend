package com.example.fingerprint_backend.domain;

import com.example.fingerprint_backend.entity.CleanMember;
import com.example.fingerprint_backend.types.CleanAttendanceStatus;

import java.util.List;

public class CleanMembers {
    private final List<CleanMember> members;

    public CleanMembers(List<CleanMember> members) {
        this.members = members;
    }

    public List<CleanMember> getMembersByStatus(CleanAttendanceStatus status) {
        return members.stream()
                .filter(member -> member.getCleanAttendanceStatus().equals(status))
                .toList();
    }

}
