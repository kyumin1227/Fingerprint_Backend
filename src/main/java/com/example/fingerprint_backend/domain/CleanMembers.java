package com.example.fingerprint_backend.domain;

import com.example.fingerprint_backend.entity.CleanMember;
import com.example.fingerprint_backend.types.CleanAttendanceStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CleanMembers {
    private final List<CleanMember> members;

    public CleanMembers(List<CleanMember> members) {
        this.members = members;
    }

    public List<CleanMember> getMembersByStatus(CleanAttendanceStatus status) {
        return members.stream()
                .filter(member -> member.getCleanAttendanceStatus().equals(status))
                .collect(Collectors.toCollection(ArrayList::new));
    }

}
