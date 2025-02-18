package com.example.fingerprint_backend.service;

import com.example.fingerprint_backend.entity.*;
import com.example.fingerprint_backend.repository.CleanGroupRepository;
import com.example.fingerprint_backend.repository.CleanScheduleRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class CleanScheduleGroupService {

    private final CleanScheduleRepository cleanScheduleRepository;
    private final CleanGroupRepository cleanGroupRepository;
    private final CleanHelperService cleanHelperService;

    @Autowired
    public CleanScheduleGroupService(CleanScheduleRepository cleanScheduleRepository, CleanGroupRepository cleanGroupRepository, CleanHelperService cleanHelperService) {
        this.cleanScheduleRepository = cleanScheduleRepository;
        this.cleanGroupRepository = cleanGroupRepository;
        this.cleanHelperService = cleanHelperService;
    }

    /**
     * 청소 스케줄을 생성하는 메소드 (취소된 스케줄은 복구, 지난 날짜 생성 불가)
     */
    public CleanSchedule createCleanSchedule(LocalDate date, String cleanAreaName, String schoolClassName) {
        cleanHelperService.validateDateIsNotPast(date);
        cleanHelperService.validateCleanScheduleByDateAndClassNameAndAreaNameIsUnique(date, cleanAreaName, schoolClassName);
        try {
            cleanHelperService.validateCleanScheduleIsCanceled(date, cleanAreaName, schoolClassName);
        } catch (IllegalStateException e) {
            // 이미 존재하는 스케줄이 취소된 경우 복구
            CleanSchedule cleanSchedule = getCleanSchedule(date, cleanAreaName, schoolClassName);
            cleanSchedule.setCanceled(false);
            return cleanSchedule;
        }
        cleanHelperService.validateCleanScheduleByDateAndClassNameAndAreaNameIsUnique(date, cleanAreaName, schoolClassName);
        CleanArea area = cleanHelperService.getCleanAreaByNameAndClassName(cleanAreaName, schoolClassName);
        SchoolClass schoolClass = cleanHelperService.getSchoolClassByName(schoolClassName);
        CleanSchedule cleanSchedule = new CleanSchedule(date, area, schoolClass);
        CleanSchedule save = cleanScheduleRepository.save(cleanSchedule);
        area.appendSchedule(save);
        schoolClass.appendSchedule(save);
        return save;
    }

    /**
     * 청소 스케줄을 가져오는 메소드
     */
    public CleanSchedule getCleanSchedule(LocalDate date, String cleanAreaName, String schoolClassName) {
        return cleanHelperService.getCleanScheduleByDateAndClassNameAndAreaName(date, cleanAreaName, schoolClassName);
    }

    /**
     * 청소 스케줄을 취소하는 메소드
     */
    public void cancelCleanSchedule(LocalDate date, String cleanAreaName, String schoolClassName) {
        CleanSchedule cleanSchedule = getCleanSchedule(date, cleanAreaName, schoolClassName);
        cleanSchedule.setCanceled(true);
    }

    /**
     * 취소된 청소 스케줄을 복구하는 메소드
     */
    public void restoreCleanSchedule(LocalDate date, String cleanAreaName, String schoolClassName) {
        CleanSchedule cleanSchedule = getCleanSchedule(date, cleanAreaName, schoolClassName);
        cleanSchedule.setCanceled(false);
    }

    /**
     * 청소 스케줄을 삭제하는 메소드
     */
    public void deleteCleanSchedule(LocalDate date, String cleanAreaName, String schoolClassName) {
        CleanSchedule cleanSchedule = getCleanSchedule(date, cleanAreaName, schoolClassName);
        cleanHelperService.getSchoolClassByName(schoolClassName).removeSchedule(cleanSchedule);
        cleanHelperService.getCleanAreaByNameAndClassName(cleanAreaName, schoolClassName).removeSchedule(cleanSchedule);
        cleanScheduleRepository.delete(cleanSchedule);
    }

    /**
     * 구역의 특정 날짜 이후의 스케줄을 가져오는 메소드
     */
    public List<CleanSchedule> getScheduleBySchoolClassNameAndAreaName(String areaName, String schoolClassName, LocalDate date) {
        CleanArea cleanArea = cleanHelperService.getCleanAreaByNameAndClassName(areaName, schoolClassName);
        return cleanScheduleRepository.findAllByDateAfterAndCleanArea(date, cleanArea);
    }

    /**
     * 반의 특정 날짜 이후의 스케줄을 가져오는 메소드
     */
    public List<CleanSchedule> getScheduleBySchoolClassName(String schoolClassName, LocalDate date) {
        SchoolClass schoolClass = cleanHelperService.getSchoolClassByName(schoolClassName);
        return cleanScheduleRepository.findAllByDateAfterAndSchoolClass(date, schoolClass);
    }

    /**
     * 취소되지 않은 마지막 청소 스케줄을 가져오는 메소드
     */
    public CleanSchedule getLastCleanSchedule(String cleanAreaName, String schoolClassName) {
        return cleanScheduleRepository.findTopBySchoolClassAndCleanAreaAndIsCanceledOrderByDateDesc(
                cleanHelperService.getSchoolClassByName(schoolClassName),
                cleanHelperService.getCleanAreaByNameAndClassName(cleanAreaName, schoolClassName),
                false
        ).orElse(null);
    }

    /**
     * 청소 그룹을 생성하는 메소드
     */
    public CleanGroup createGroup(String cleanAreaName, String schoolClassName, int memberCount) {
        CleanArea cleanArea = cleanHelperService.getCleanAreaByNameAndClassName(cleanAreaName, schoolClassName);
        SchoolClass schoolClass = cleanHelperService.getSchoolClassByName(schoolClassName);
        CleanGroup cleanGroup = new CleanGroup(cleanArea, schoolClass, memberCount, new ArrayList<>(memberCount));
        CleanGroup save = cleanGroupRepository.save(cleanGroup);
        cleanArea.appendGroup(save);
        return save;
    }

    /**
     * 청소 그룹을 생성하는 메소드 (클래스)
     */
    public CleanGroup createGroup(CleanArea cleanArea, SchoolClass schoolClass, int memberCount) {
        CleanGroup cleanGroup = new CleanGroup(cleanArea, schoolClass, memberCount, new ArrayList<>(memberCount));
        CleanGroup save = cleanGroupRepository.save(cleanGroup);
        cleanArea.appendGroup(save);
        return save;
    }

    /**
     * 청소 그룹에 멤버를 추가하는 메소드
     */
    public void appendMemberToGroup(Long groupId, String studentNumber) {
        CleanMember member = cleanHelperService.getCleanMemberByStudentNumber(studentNumber);
        CleanGroup cleanGroup = cleanHelperService.getCleanGroupById(groupId);
        cleanGroup.appendMember(member);
    }

    /**
     * 청소 그룹에서 멤버를 제거하는 메소드
     */
    public void removeMemberFromGroup(Long groupId, String studentNumber) {
        CleanMember member = cleanHelperService.getCleanMemberByStudentNumber(studentNumber);
        CleanGroup cleanGroup = cleanHelperService.getCleanGroupById(groupId);
        cleanGroup.removeMember(member);
    }

    /**
     * 구역 이름과 학급 이름으로 청소 그룹을 가져오는 메소드
     */
    public List<CleanGroup> getGroupsByAreaNameAndClassName(String areaName, String schoolClassName) {
        CleanArea cleanArea = cleanHelperService.getCleanAreaByNameAndClassName(areaName, schoolClassName);
        return cleanArea.getGroups();
    }

    /**
     * 구역 이름과 학급 이름으로 청소 그룹을 가져오는 메소드 (청소 여부)
     */
    public List<CleanGroup> getGroupsByAreaNameAndClassNameAndIsCleaned(String areaName, String schoolClassName, boolean isCleaned) {
        CleanArea cleanArea = cleanHelperService.getCleanAreaByNameAndClassName(areaName, schoolClassName);
        return cleanGroupRepository.findByIsCleanedAndCleanArea(isCleaned, cleanArea);
    }

    /**
     * 청소 그룹의 멤버 수를 세팅하는 메소드
     */
    public void setMemberCount(Long groupId, int memberCount) {
        CleanGroup group = cleanHelperService.getCleanGroupById(groupId);
        group.changeMemberCount(memberCount);
    }

    /**
     * 랜덤으로 그룹을 생성하는 메소드
     */
    private void createGroupByRandom(CleanArea cleanArea, SchoolClass schoolClass, List<CleanMember> members, int memberCount) {
        CleanGroup group = createGroup(cleanArea, schoolClass, memberCount);

        int append = Math.min(memberCount, members.size());

        for (int i = 0; i < append; i++) {
            int randomNum = (int) (Math.random() * members.size());
            group.appendMember(members.remove(randomNum));
        }
    }

    /**
     * 랜덤으로 그룹들을 생성하는 메소드
     */
    public void createGroupsByRandom(String areaName, String schoolClassName, List<CleanMember> members, double groupMemberCount) {
        fillLastGroupByRandom(areaName, schoolClassName, members);
        CleanArea cleanArea = cleanHelperService.getCleanAreaByNameAndClassName(areaName, schoolClassName);
        SchoolClass schoolClass = cleanHelperService.getSchoolClassByName(schoolClassName);
        if (members == null || members.isEmpty()) {
            throw new IllegalArgumentException("리스트가 비어있습니다.");
        } else if (groupMemberCount < 1) {
            throw new IllegalArgumentException("그룹의 최대 인원은 1보다 작을 수 없습니다.");
        }
        int groupCount = (int) Math.ceil(members.size() / groupMemberCount);

        for (int i = 0; i < groupCount; i++) {
            createGroupByRandom(cleanArea, schoolClass, members, (int) groupMemberCount);
        }
    }

    /**
     * 랜덤으로 마지막 그룹을 채우는 메소드
     * WARNING: 추가할 멤버 수가 마지막 그룹의 멤버 수 보다 적을 경우 채우지 않음
     */
    public void fillLastGroupByRandom(String areaName, String schoolClassName, List<CleanMember> members) {
        CleanGroup lastGroup = getLastGroup(areaName, schoolClassName);
        if (lastGroup != null && members.size() >= lastGroup.getMemberCount() - lastGroup.getMembers().size()) {
            int empty = lastGroup.getMemberCount() - lastGroup.getMembers().size();
            for (int i = 0; i < empty; i++) {
                int randomNum = (int) (Math.random() * members.size());
                CleanMember remove = members.remove(randomNum);
                try {
                    lastGroup.appendMember(remove);
                } catch (IllegalArgumentException e) {
//                    중복 멤버가 발생할 경우 다시 추가
                    members.add(remove);
                    i--;
                }
            }
        }
    }

    /**
     * 마지막 그룹을 가져오는 메소드 (청소 하지 않은 그룹)
     */
    public CleanGroup getLastGroup(String areaName, String schoolClassName) {
        CleanArea cleanArea = cleanHelperService.getCleanAreaByNameAndClassName(areaName, schoolClassName);
        return cleanGroupRepository.findTopByCleanAreaAndIsCleanedOrderByIdDesc(
                cleanArea, false
        ).orElse(null);
    }

    /**
     * 청소 스케줄을 생성하는 메소드
     * date 이후 cycle 일 주기로 days 요일에 총 count 개의 스케줄을 생성
     */
    public void createCleanSchedules(LocalDate date, String areaName, String schoolClassName, int cycle, Set<DayOfWeek> days, int count) {
        cleanHelperService.validateCreateSchedule(date, cycle, days, count);
        int day = 1;
        LocalDate targetDate = date.plusDays(day);
        while (count > 0) {
            targetDate = date.plusDays(day);
            if (days.contains(targetDate.getDayOfWeek())) {
                try {
                    createCleanSchedule(targetDate, areaName, schoolClassName);
                    count--;
                } catch (IllegalArgumentException e) {
                    // 이미 존재하는 스케줄인 경우
                }
            }

            if (targetDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                day += (cycle - 1) * 7;
            }
            day++;
        }
        CleanArea cleanArea = cleanHelperService.getCleanAreaByNameAndClassName(areaName, schoolClassName);
        cleanArea.setLastScheduledDate(targetDate);
    }
}
