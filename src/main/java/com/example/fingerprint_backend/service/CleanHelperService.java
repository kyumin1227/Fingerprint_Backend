package com.example.fingerprint_backend.service;

import com.example.fingerprint_backend.entity.*;
import com.example.fingerprint_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class CleanHelperService {

    private final SchoolClassRepository schoolClassRepository;
    private final CleanMemberRepository cleanMemberRepository;
    private final CleanAreaRepository cleanAreaRepository;
    private final CleanScheduleRepository cleanScheduleRepository;
    private final CleanGroupRepository cleanGroupRepository;

    @Autowired
    public CleanHelperService(SchoolClassRepository schoolClassRepository, CleanMemberRepository cleanMemberRepository, CleanAreaRepository cleanAreaRepository, CleanGroupService cleanGroupService, CleanScheduleRepository cleanScheduleRepository, CleanGroupRepository cleanGroupRepository) {
        this.schoolClassRepository = schoolClassRepository;
        this.cleanMemberRepository = cleanMemberRepository;
        this.cleanAreaRepository = cleanAreaRepository;
        this.cleanScheduleRepository = cleanScheduleRepository;
        this.cleanGroupRepository = cleanGroupRepository;
    }

    /**
     * 반 이름으로 SchoolClass 엔티티를 가져오는 메소드
     * @throws IllegalArgumentException 존재하지 않는 반 이름일 경우
     */
    public SchoolClass getSchoolClassByName(String schoolClassName) {
        return schoolClassRepository.findSchoolClassByName(schoolClassName)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 반 이름입니다."));
    }

    /**
     * 반 이름이 존재하는지 확인하는 메소드
     * @throws IllegalArgumentException 존재하지 않는 반 이름일 경우
     */
    public void validateSchoolClassExistsByName(String schoolClassName) {
        boolean isExist = schoolClassRepository.existsSchoolClassByName(schoolClassName);
        if (!isExist) {
            throw new IllegalArgumentException("존재하지 않는 반 이름입니다.");
        }
    }

    /**
     * 반 이름이 유일한지 확인하는 메소드
     * @throws IllegalArgumentException 이미 존재하는 반 이름일 경우
     */
    public void validateSchoolClassNameIsUnique(String schoolClassName) {
        boolean isExist = schoolClassRepository.existsSchoolClassByName(schoolClassName);
        if (isExist) {
            throw new IllegalArgumentException("이미 존재하는 반 이름입니다.");
        }
    }

    /**
     * 학번으로 CleanMember 엔티티를 가져오는 메소드
     * @throws IllegalArgumentException 존재하지 않는 학번일 경우
     */
    public CleanMember getCleanMemberByStudentNumber(String studentNumber) {
        return cleanMemberRepository.findByStudentNumber(studentNumber)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 학번입니다."));
    }

    /**
     * 학번이 존재하는지 확인하는 메소드
     * @throws IllegalArgumentException 존재하지 않는 학번일 경우
     */
    public void validateCleanMemberExistsByStudentNumber(String studentNumber) {
        boolean isExist = cleanMemberRepository.existsByStudentNumber(studentNumber);
        if (!isExist) {
            throw new IllegalArgumentException("존재하지 않는 학번입니다.");
        }
    }

    /**
     * 학번이 유일한지 확인하는 메소드
     * @throws IllegalArgumentException 이미 존재하는 학번일 경우
     */
    public void validateStudentNumberIsUnique(String studentNumber) {
        boolean isExist = cleanMemberRepository.existsByStudentNumber(studentNumber);
        if (isExist) {
            throw new IllegalArgumentException("이미 존재하는 학번입니다.");
        }
    }

    /**
     * 청소 구역 이름과 반 이름으로 CleanArea 엔티티를 가져오는 메소드
     * @throws IllegalArgumentException 존재하지 않는 청소 구역 이름일 경우
     */
    public CleanArea getCleanAreaByNameAndClassName(String areaName, String schoolClassName) {
        SchoolClass schoolClass = getSchoolClassByName(schoolClassName);
        return cleanAreaRepository.findByNameAndSchoolClass(areaName, schoolClass)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 청소 구역 이름입니다."));
    }

    /**
     * 청소 구역 이름과 반 이름이 존재하는지 확인하는 메소드
     * @throws IllegalArgumentException 존재하지 않는 청소 구역 이름일 경우
     */
    public void validateCleanAreaExistsByAreaNameAndClassName(String areaName, String schoolClassName) {
        SchoolClass schoolClass = getSchoolClassByName(schoolClassName);
        boolean isExist = cleanAreaRepository.existsByNameAndSchoolClass(areaName, schoolClass);
        if (!isExist) {
            throw new IllegalArgumentException("존재하지 않는 청소 구역 이름입니다.");
        }
    }

    /**
     * 청소 구역 이름과 반 이름으로 청소 구역이 유일한지 확인하는 메소드
     * @throws IllegalArgumentException 이미 존재하는 청소 구역 이름일 경우
     */
    public void validateAreaNameAndClassNameIsUnique(String areaName, String schoolClassName) {
        SchoolClass schoolClass = getSchoolClassByName(schoolClassName);
        boolean isExist = cleanAreaRepository.existsByNameAndSchoolClass(areaName, schoolClass);
        if (isExist) {
            throw new IllegalArgumentException("이미 존재하는 청소 구역 이름입니다.");
        }
    }

    /**
     * 청소 일정을 가져오는 메소드
     * @throws IllegalArgumentException 존재하지 않는 청소 일정일 경우
     */
    public CleanSchedule getCleanScheduleByDateAndClassNameAndAreaName(LocalDate date, String schoolClassName, String areaName) {
        SchoolClass schoolClass = getSchoolClassByName(schoolClassName);
        CleanArea cleanArea = getCleanAreaByNameAndClassName(areaName, schoolClassName);
        return cleanScheduleRepository.findByDateAndSchoolClassAndCleanArea(date, schoolClass, cleanArea)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 청소 일정입니다."));
    }

    /**
     * 청소 일정이 존재하는지 확인하는 메소드
     * @throws IllegalArgumentException 존재하지 않는 청소 일정일 경우
     */
    public void validateCleanScheduleExistsByDateAndClassNameAndAreaName(LocalDate date, String schoolClassName, String areaName) {
        SchoolClass schoolClass = getSchoolClassByName(schoolClassName);
        CleanArea cleanArea = getCleanAreaByNameAndClassName(areaName, schoolClassName);
        boolean isExist = cleanScheduleRepository.existsByDateAndSchoolClassAndCleanArea(date, schoolClass, cleanArea);
        if (!isExist) {
            throw new IllegalArgumentException("존재하지 않는 청소 일정입니다.");
        }
    }

    /**
     * 청소 그룹이 존재하는지 확인하는 메소드
     * @throws IllegalArgumentException 존재하지 않는 청소 그룹일 경우
     */
    public void validateCleanGroupExists(CleanGroup cleanGroup) {
        boolean isExist = cleanGroupRepository.existsById(cleanGroup.getId());
        if (!isExist) {
            throw new IllegalArgumentException("존재하지 않는 청소 그룹입니다.");
        }
    }
}
