package com.example.fingerprint_backend.service;

import com.example.fingerprint_backend.entity.CleanGroup;
import com.example.fingerprint_backend.entity.CleanMember;
import com.example.fingerprint_backend.repository.CleanGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class CleanGroupService {

    private final CleanGroupRepository cleanGroupRepository;

    @Autowired
    public CleanGroupService(CleanGroupRepository cleanGroupRepository) {
        this.cleanGroupRepository = cleanGroupRepository;
    }

    /**
     * 청소 그룹을 생성하는 메소드
     */
    public CleanGroup createGroup(int memberCount) {
        CleanGroup cleanGroup = new CleanGroup(memberCount);
        return cleanGroupRepository.save(cleanGroup);
    }

    /**
     * 청소 그룹을 생성하는 메소드 (멤버 포함)
     */
    public CleanGroup createGroup(int memberCount, Set<CleanMember> members) {
        CleanGroup cleanGroup = new CleanGroup(memberCount, members);
        return cleanGroupRepository.save(cleanGroup);
    }

    /**
     * 청소 그룹에 멤버를 세팅하는 메소드
     */
    public CleanGroup setMembers(CleanGroup cleanGroup, Set<CleanMember> members) {
        if (!cleanGroupRepository.existsCleanGroupById(cleanGroup.getId())) {
            throw new IllegalArgumentException("존재하지 않는 청소 그룹입니다.");
        }
        cleanGroup.setMembers(members);
        return cleanGroupRepository.save(cleanGroup);
    }

    /**
     * 청소 그룹에 멤버를 추가하는 메소드
     */
    public CleanGroup appendMember(CleanGroup cleanGroup, CleanMember member) {
        if (!cleanGroupRepository.existsCleanGroupById(cleanGroup.getId())) {
            throw new IllegalArgumentException("존재하지 않는 청소 그룹입니다.");
        }
        cleanGroup.appendMember(member);
        return cleanGroupRepository.save(cleanGroup);
    }

    /**
     * 청소 그룹에서 멤버를 제거하는 메소드
     */
    public CleanGroup removeMember(CleanGroup cleanGroup, CleanMember member) {
        if (!cleanGroupRepository.existsCleanGroupById(cleanGroup.getId())) {
            throw new IllegalArgumentException("존재하지 않는 청소 그룹입니다.");
        }
        cleanGroup.removeMember(member);
        return cleanGroupRepository.save(cleanGroup);
    }

    /**
     * 청소 그룹을 삭제하는 메소드
     */
    public void deleteGroup(CleanGroup cleanGroup) {
        if (!cleanGroupRepository.existsCleanGroupById(cleanGroup.getId())) {
            throw new IllegalArgumentException("존재하지 않는 청소 그룹입니다.");
        }
        cleanGroupRepository.delete(cleanGroup);
    }

}
