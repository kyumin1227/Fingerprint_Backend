//package com.example.fingerprint_backend.service;
//
//import com.example.fingerprint_backend.entity.CleanGroup;
//import com.example.fingerprint_backend.entity.CleanMember;
//import com.example.fingerprint_backend.entity.SchoolClass;
//import com.example.fingerprint_backend.repository.CleanGroupRepository;
//import jakarta.transaction.Transactional;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//@Service
//public class CleanGroupService {
//
//    private final CleanGroupRepository cleanGroupRepository;
//
//    @Autowired
//    public CleanGroupService(CleanGroupRepository cleanGroupRepository) {
//        this.cleanGroupRepository = cleanGroupRepository;
//    }
//
//    /**
//     * 청소 그룹을 생성하는 메소드
//     */
//    public CleanGroup createGroup(SchoolClass schoolClass, int memberCount, Set<CleanMember> members) {
//        CleanGroup cleanGroup = new CleanGroup(schoolClass, memberCount, members);
//        return cleanGroupRepository.save(cleanGroup);
//    }
//
//    /**
//     * 그룹이 존재하는지 확인하는 메소드
//     */
//    public void validateGroupExists(CleanGroup cleanGroup) {
//        if (!cleanGroupRepository.existsCleanGroupById(cleanGroup.getId())) {
//            throw new IllegalArgumentException("존재하지 않는 청소 그룹입니다.");
//        }
//    }
//
//    /**
//     * 청소 그룹에 멤버를 세팅하는 메소드
//     */
//    public CleanGroup setMembers(CleanGroup cleanGroup, Set<CleanMember> members) {
//        validateGroupExists(cleanGroup);
//        cleanGroup.setMembers(members);
//        return cleanGroupRepository.save(cleanGroup);
//    }
//
//    /**
//     * 청소 그룹의 멤버 수를 세팅하는 메소드
//     */
//    public CleanGroup setMemberCount(CleanGroup cleanGroup, int memberCount) {
//        validateGroupExists(cleanGroup);
//        cleanGroup.setMemberCount(memberCount);
//        return cleanGroupRepository.save(cleanGroup);
//    }
//
//    /**
//     * 청소 그룹에 멤버를 추가하는 메소드
//     */
//    public CleanGroup appendMember(CleanGroup cleanGroup, CleanMember member) {
//        validateGroupExists(cleanGroup);
//        cleanGroup.appendMember(member);
//        return cleanGroupRepository.save(cleanGroup);
//    }
//
//    /**
//     * 청소 그룹에서 멤버를 제거하는 메소드
//     * 그룹에 멤버가 없을 경우 그룹을 삭제한다.
//     */
//    public CleanGroup removeMember(CleanGroup cleanGroup, CleanMember member) {
//        validateGroupExists(cleanGroup);
//        cleanGroup.removeMember(member);
//        if (cleanGroup.getMembers().isEmpty()) {
//            deleteGroup(cleanGroup);
//            return null;
//        }
//        return cleanGroupRepository.save(cleanGroup);
//    }
//
//    /**
//     * 청소 그룹을 삭제하는 메소드
//     */
//    public void deleteGroup(CleanGroup cleanGroup) {
//        validateGroupExists(cleanGroup);
//        cleanGroupRepository.delete(cleanGroup);
//    }
//
//    /**
//     * 학생 리스트에서 랜덤으로 학생들을 가져오고 리스트에서 제거하는 메소드
//     *
//     * @param members 학생 리스트
//     * @param count   뽑을 학생 수
//     * @return 랜덤으로 선택된 학생
//     * <p>
//     * TODO: 만약 리스트에 중복된 학생이 있다면 Set 이기 때문에 사라질 가능성 있음.
//     */
//    private Set<CleanMember> getMembersByRandom(List<CleanMember> members, int count) {
//        if (members.size() < count) {
//            throw new IllegalArgumentException("남은 학생 수가 뽑을 학생 수보다 적습니다.");
//        }
//        Set<CleanMember> selectedMembers = new HashSet<>();
//        for (int i = 0; i < count; i++) {
//            int randomNum = (int) (Math.random() * members.size());
//            selectedMembers.add(members.remove(randomNum));
//        }
//        if (selectedMembers.size() != count) {
//            throw new IllegalArgumentException("중복된 학생이 존재합니다.");
//        }
//        return selectedMembers;
//    }
//
//    /**
//     * 랜덤으로 그룹을 생성하는 메소드
//     */
//    private CleanGroup createGroupByRandom(SchoolClass schoolClass, List<CleanMember> members, int memberCount) {
//        Set<CleanMember> membersByRandom = getMembersByRandom(members, Math.min(memberCount, members.size()));
//
//        return createGroup(schoolClass, memberCount, membersByRandom);
//    }
//
//    /**
//     * 랜덤으로 그룹들을 생성하는 메소드
//     *
//     * @return 생성된 그룹들
//     */
//    public List<CleanGroup> createGroupsByRandom(SchoolClass schoolClass, List<CleanMember> members, double groupMemberCount) {
//        if (members == null || members.isEmpty()) {
//            throw new IllegalArgumentException("리스트가 비어있습니다.");
//        } else if (groupMemberCount < 1) {
//            throw new IllegalArgumentException("그룹의 최대 인원은 1보다 작을 수 없습니다.");
//        }
//        int groupCount = (int) Math.ceil(members.size() / groupMemberCount);
//        List<CleanGroup> groups = new ArrayList<>(groupCount);
//
//        for (int i = 0; i < groupCount; i++) {
//            CleanGroup groupByRandom = createGroupByRandom(schoolClass, members, (int) groupMemberCount);
//            groups.add(groupByRandom);
//        }
//
//        return groups;
//    }
//}
