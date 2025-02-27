package com.example.fingerprint_backend.service;

import com.example.fingerprint_backend.entity.SchoolClass;
import com.example.fingerprint_backend.entity.CleanArea;
import com.example.fingerprint_backend.entity.CleanMember;
import com.example.fingerprint_backend.repository.SchoolClassRepository;
import com.example.fingerprint_backend.repository.CleanAreaRepository;
import com.example.fingerprint_backend.repository.CleanMemberRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class CleanManagementService {

    private final SchoolClassRepository schoolClassRepository;
    private final CleanMemberRepository cleanMemberRepository;
    private final CleanAreaRepository cleanAreaRepository;
    private final CleanHelperService cleanHelperService;
    private final CleanScheduleGroupService cleanScheduleGroupService;

    @Autowired
    public CleanManagementService(SchoolClassRepository schoolClassRepository, CleanMemberRepository cleanMemberRepository, CleanAreaRepository cleanAreaRepository, CleanHelperService cleanHelperService, CleanScheduleGroupService cleanScheduleGroupService) {
        this.schoolClassRepository = schoolClassRepository;
        this.cleanMemberRepository = cleanMemberRepository;
        this.cleanAreaRepository = cleanAreaRepository;
        this.cleanHelperService = cleanHelperService;
        this.cleanScheduleGroupService = cleanScheduleGroupService;
    }

    /**
     * 반 이름으로 반을 생성하는 메소드
     */
    public SchoolClass createSchoolClass(String schoolClassName) {
        cleanHelperService.validateSchoolClassNameIsUnique(schoolClassName);
        return schoolClassRepository.save(new SchoolClass(schoolClassName));
    }

    /**
     * 청소 학생을 생성하는 메소드
     */
    public CleanMember createMember(String studentNumber, String givenName, String familyName, Long schoolClassId, CleanArea cleanArea) {
        cleanHelperService.validateStudentNumberIsUnique(studentNumber);
        SchoolClass schoolClass = cleanHelperService.getSchoolClassById(schoolClassId);
        CleanMember member = new CleanMember(studentNumber, givenName, familyName, schoolClass, cleanArea);
        member.setCleanArea(schoolClass.getDefaultArea());
        CleanMember save = cleanMemberRepository.save(member);
        schoolClass.appendCleanMember(save);
        return save;
    }

    /**
     * 반 이름으로 학생들을 가져온다.
     */
    public List<CleanMember> getMembersBySchoolClassName(String schoolClassName) {
        SchoolClass schoolClass = cleanHelperService.getSchoolClassByName(schoolClassName);
        return schoolClass.getClassCleanMembers();
    }

    /**
     * 청소 구역을 생성하는 메소드
     */
    public CleanArea createArea(String areaName, Long schoolClassId) {
        cleanHelperService.validateAreaNameAndClassIdIsUnique(areaName, schoolClassId);
        SchoolClass schoolClass = cleanHelperService.getSchoolClassById(schoolClassId);
        CleanArea save = cleanAreaRepository.save(new CleanArea(areaName, schoolClass, Set.of(), 0));
        if (schoolClass.getDefaultArea() == null) {
            schoolClass.setDefaultArea(save);
        }
        return save;
    }

    /**
     * 청소 구역을 생성하는 메소드
     */
    public CleanArea createArea(String areaName, Long schoolClassId, Set<DayOfWeek> days, Integer cycle) {
        cleanHelperService.validateAreaNameAndClassIdIsUnique(areaName, schoolClassId);
        SchoolClass schoolClass = cleanHelperService.getSchoolClassById(schoolClassId);
        CleanArea save = cleanAreaRepository.save(new CleanArea(areaName, schoolClass, days, cycle));
        if (schoolClass.getDefaultArea() == null) {
            schoolClass.setDefaultArea(save);
        }
        return save;
    }

    /**
     * 학급의 기본 청소 구역을 설정하는 메소드
     */
    public void setDefaultArea(String areaName, Long schoolClassId) {
        cleanHelperService.validateCleanAreaExistsByAreaNameAndClassId(areaName, schoolClassId);
        SchoolClass schoolClass = cleanHelperService.getSchoolClassById(schoolClassId);
        CleanArea area = cleanHelperService.getCleanAreaByNameAndClassId(areaName, schoolClassId);
        schoolClass.setDefaultArea(area);
        schoolClassRepository.save(schoolClass);
    }

    /**
     * 학생의 청소 구역을 설정하는 메소드, 기존 구역에서 제거 후 새로운 구역으로 설정
     */
    public void setMemberCleanArea(String studentNumber, String areaName) {
        CleanMember member = cleanHelperService.getCleanMemberByStudentNumber(studentNumber);
        if (member.getCleanArea() != null) {
            cleanScheduleGroupService.getGroupsByAreaNameAndClassIdAndIsCleaned(member.getCleanArea().getName(), member.getSchoolClass().getId(), false)
                    .forEach(group -> {
                        try {
                            group.removeMember(member);
                        } catch (IllegalArgumentException e) {
                            // 멤버가 그룹에 존재하지 않는 경우
                        }
                    });
        }
        CleanArea cleanArea = cleanHelperService.getCleanAreaByNameAndClassName(areaName, member.getSchoolClass().getName());
        member.setCleanArea(cleanArea);
    }

    /**
     * 반 이름과 구역으로 특정 구역의 학생들을 가져오는 메소드 (복사본)
     */
    public List<CleanMember> getMembersByAreaNameAndClassId(String areaName, Long schoolClassId) {
        CleanArea cleanArea = cleanHelperService.getCleanAreaByNameAndClassId(areaName, schoolClassId);
        return new ArrayList<>(cleanArea.getMembers());
    }

    /**
     * 학생을 삭제하는 메소드 (반, 구역, 남은 청소 그룹에서 삭제 후 삭제 표시)
     */
    @Transactional
    public void deleteMember(String studentNumber) {
        CleanMember member = cleanHelperService.getCleanMemberByStudentNumber(studentNumber);
        member.getSchoolClass().removeCleanMember(member);
        CleanArea cleanArea = member.getCleanArea();
        cleanArea.removeMember(member);
        cleanScheduleGroupService.getGroupsByAreaNameAndClassIdAndIsCleaned(cleanArea.getName(), member.getSchoolClass().getId(), false)
                .forEach(group -> {
                    try {
                        group.removeMember(member);
                    } catch (IllegalArgumentException e) {
                        // 멤버가 그룹에 존재하지 않는 경우
                    }
                });
        member.setIsDeleted(true);
    }

    /**
     * 학생 정보를 수정하는 메소드
     */
    @Transactional
    public CleanMember updateMember(String studentNumber, String givenName, String familyName, String areaName) {
        CleanMember member = cleanHelperService.getCleanMemberByStudentNumber(studentNumber);
        if (givenName != null) {
            member.setGivenName(givenName);
        }
        if (familyName != null) {
            member.setFamilyName(familyName);
        }
        setMemberCleanArea(studentNumber, areaName);
        return member;
    }
}
