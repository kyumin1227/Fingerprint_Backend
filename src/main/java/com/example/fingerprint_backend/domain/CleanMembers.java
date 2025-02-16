package com.example.fingerprint_backend.domain;

import com.example.fingerprint_backend.entity.CleanArea;
import com.example.fingerprint_backend.entity.CleanMember;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CleanMembers {
    private final List<CleanMember> members;

    public CleanMembers(List<CleanMember> members) {
        this.members = members;
    }

    public List<CleanMember> getMembersByArea(CleanArea area) {
        return members.stream()
                .filter(member -> member.getCleanArea().equals(area))
                .collect(Collectors.toCollection(ArrayList::new));
    }

}
