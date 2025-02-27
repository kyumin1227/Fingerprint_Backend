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

    @Autowired
    public CleanManagementService(SchoolClassRepository schoolClassRepository, CleanMemberRepository cleanMemberRepository, CleanAreaRepository cleanAreaRepository, CleanHelperService cleanHelperService) {
        this.schoolClassRepository = schoolClassRepository;
        this.cleanMemberRepository = cleanMemberRepository;
        this.cleanAreaRepository = cleanAreaRepository;
        this.cleanHelperService = cleanHelperService;
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
     * 학생의 청소 구역을 설정하는 메소드
     */
    public void setMemberCleanArea(String studentNumber, String areaName) {
        CleanMember member = cleanHelperService.getCleanMemberByStudentNumber(studentNumber);
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
}
