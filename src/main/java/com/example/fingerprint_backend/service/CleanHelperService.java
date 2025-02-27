package com.example.fingerprint_backend.service;

import com.example.fingerprint_backend.entity.*;
import com.example.fingerprint_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Set;

@Service
public class CleanHelperService {

    private final SchoolClassRepository schoolClassRepository;
    private final CleanMemberRepository cleanMemberRepository;
    private final CleanAreaRepository cleanAreaRepository;
    private final CleanScheduleRepository cleanScheduleRepository;
    private final CleanGroupRepository cleanGroupRepository;

    @Autowired
    public CleanHelperService(SchoolClassRepository schoolClassRepository, CleanMemberRepository cleanMemberRepository, CleanAreaRepository cleanAreaRepository, CleanScheduleRepository cleanScheduleRepository, CleanGroupRepository cleanGroupRepository) {
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
     * 반 아이디로 SchoolClass 엔티티를 가져오는 메소드
     * @throws IllegalArgumentException 존재하지 않는 반 이름일 경우
     */
    public SchoolClass getSchoolClassById(Long schoolClassId) {
        return schoolClassRepository.findById(schoolClassId)
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
     * 청소 구역 이름과 반 아이디로 CleanArea 엔티티를 가져오는 메소드
     * @throws IllegalArgumentException 존재하지 않는 청소 구역 이름일 경우
     */
    public CleanArea getCleanAreaByNameAndClassId(String areaName, Long schoolClassId) {
        SchoolClass schoolClass = getSchoolClassById(schoolClassId);
        return cleanAreaRepository.findByNameAndSchoolClass(areaName, schoolClass)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 청소 구역 이름입니다."));
    }

    /**
     * 청소 구역 이름과 반 아이디가 존재하는지 확인하는 메소드
     * @throws IllegalArgumentException 존재하지 않는 청소 구역 이름일 경우
     */
    public void validateCleanAreaExistsByAreaNameAndClassId(String areaName, Long schoolClassId) {
        SchoolClass schoolClass = getSchoolClassById(schoolClassId);
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
     * 청소 구역 이름과 반 아이디로 청소 구역이 유일한지 확인하는 메소드
     * @throws IllegalArgumentException 이미 존재하는 청소 구역 이름일 경우
     */
    public void validateAreaNameAndClassIdIsUnique(String areaName, Long schoolClassId) {
        SchoolClass schoolClass = getSchoolClassById(schoolClassId);
        boolean isExist = cleanAreaRepository.existsByNameAndSchoolClass(areaName, schoolClass);
        if (isExist) {
            throw new IllegalArgumentException("이미 존재하는 청소 구역 이름입니다.");
        }
    }

    /**
     * 청소 스케줄을 가져오는 메소드
     * @throws IllegalArgumentException 존재하지 않는 청소 스케줄일 경우
     */
    public CleanSchedule getCleanScheduleByDateAndAreaNameAndClassId(LocalDate date, String areaName, Long schoolClassId) {
        SchoolClass schoolClass = getSchoolClassById(schoolClassId);
        CleanArea cleanArea = getCleanAreaByNameAndClassId(areaName, schoolClassId);
        return cleanScheduleRepository.findByDateAndSchoolClassAndCleanArea(date, schoolClass, cleanArea)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 청소 스케줄입니다."));
    }

    /**
     * 청소 스케줄이 생성되었는지 확인하는 메소드
     * @throws IllegalArgumentException 생성 되지 않은 청소 스케줄일 경우
     */
    public void validateCleanScheduleExistsByDateAndClassNameAndAreaName(LocalDate date, String areaName, String schoolClassName) {
        SchoolClass schoolClass = getSchoolClassByName(schoolClassName);
        CleanArea cleanArea = getCleanAreaByNameAndClassName(areaName, schoolClassName);
        boolean isExist = cleanScheduleRepository.existsByDateAndSchoolClassAndCleanArea(date, schoolClass, cleanArea);
        if (!isExist) {
            throw new IllegalArgumentException("존재하지 않는 청소 스케줄입니다.");
        }
    }

    /**
     * 취소되지 않은 청소 스케줄이 있는지 확인하는 메소드
     * @throws IllegalArgumentException 취소되지 않은 청소 스케줄이 존재하는 경우
     */
    public void validateCleanScheduleByDateAndClassNameAndAreaNameIsUnique(LocalDate date, String areaName, Long schoolClassId) {
        SchoolClass schoolClass = getSchoolClassById(schoolClassId);
        CleanArea cleanArea = getCleanAreaByNameAndClassId(areaName, schoolClassId);
        boolean isExist = cleanScheduleRepository.existsByDateAndSchoolClassAndCleanAreaAndIsCanceled(date, schoolClass, cleanArea, false);
        if (isExist) {
            throw new IllegalArgumentException("이미 존재하는 청소 스케줄입니다.");
        }
    }

    /**
     * 취소된 청소 스케줄인지 확인하는 메소드
     * @throws IllegalStateException 취소된 청소 스케줄일 경우
     */
    public void validateCleanScheduleIsCanceled(LocalDate date, String areaName, Long schoolClassId) {
        SchoolClass schoolClass = getSchoolClassById(schoolClassId);
        CleanArea cleanArea = getCleanAreaByNameAndClassId(areaName, schoolClassId);
        boolean isExist = cleanScheduleRepository.existsByDateAndSchoolClassAndCleanAreaAndIsCanceled(date, schoolClass, cleanArea, true);
        if (isExist) {
            throw new IllegalStateException("취소된 청소 스케줄입니다.");
        }
    }

    /**
     * 날짜가 과거인지 확인하는 메소드
     * @throws IllegalArgumentException 과거의 날짜일 경우
     */
    public void validateDateIsNotPast(LocalDate date) {
        if (date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("과거의 날짜는 입력할 수 없습니다.");
        }
    }

    /**
     * 날짜가 미래인지 확인하는 메소드
     * @throws IllegalArgumentException 미래의 날짜일 경우
     */
    public void validateDateIsNotFuture(LocalDate date) {
        if (date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("미래의 날짜는 입력할 수 없습니다.");
        }
    }

    /**
     * 청소 그룹을 가져오는 메소드
     * @throws IllegalArgumentException 존재하지 않는 청소 그룹일 경우
     */
    public CleanGroup getCleanGroupById(Long groupId) {
        return cleanGroupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 청소 그룹입니다."));
    }

    /**
     * 청소 그룹이 존재하는지 확인하는 메소드
     * @throws IllegalArgumentException 존재하지 않는 청소 그룹일 경우
     */
    public void validateCleanGroupExists(Long groupId) {
        boolean isExist = cleanGroupRepository.existsById(groupId);
        if (!isExist) {
            throw new IllegalArgumentException("존재하지 않는 청소 그룹입니다.");
        }
    }

    /**
     * 스케줄 생성 시 유효성 검사
     * @throws IllegalArgumentException 과거의 날짜일 경우
     * @throws IllegalArgumentException 주기가 0보다 작을 경우
     * @throws IllegalArgumentException 요일이 선택되지 않았을 경우
     * @throws IllegalArgumentException 생성 갯수가 0보다 작을 경우
     */
    public void validateCreateSchedule(LocalDate date, int cycle, Set<DayOfWeek> days, int count) {
        validateDateIsNotPast(date);
        if (cycle < 0) {
            throw new IllegalArgumentException("주기는 0보다 작을 수 없습니다.");
        }
        if (days == null || days.isEmpty()) {
            throw new IllegalArgumentException("요일은 하나 이상 선택해야 합니다.");
        }
        if (count < 0) {
            throw new IllegalArgumentException("생성 갯수는 0보다 작을 수 없습니다.");
        }
    }

    /**
     * 스케줄 완료 시 유효성 검사
     * @throws IllegalArgumentException 취소된 청소 스케줄일 경우
     * @throws IllegalArgumentException 이미 완료된 청소 스케줄일 경우
     */
    public void validateScheduleComplete(CleanSchedule cleanSchedule) {
        if (cleanSchedule.isCanceled()) {
            throw new IllegalArgumentException("취소된 청소 스케줄입니다.");
        }
        if (cleanSchedule.isCompleted()) {
            throw new IllegalArgumentException("이미 완료된 청소 스케줄입니다.");
        }
    }

    /**
     * 조회할 청소 그룹이 없을 경우
     * @throws IllegalStateException 조회할 청소 그룹이 없을 경우
     */
    public void validateCleanGroupNotEmpty(List<CleanGroup> groups) {
        if (groups == null || groups.isEmpty()) {
            throw new IllegalStateException("조회할 청소 그룹이 없습니다.");
        }
    }

    /**
     * 조회할 청소 스케줄이 없을 경우
     * @throws IllegalStateException 조회할 청소 스케줄이 없을 경우
     */
    public void validateCleanScheduleNotEmpty(List<CleanSchedule> schedules) {
        if (schedules == null || schedules.isEmpty()) {
            throw new IllegalStateException("조회할 청소 스케줄이 없습니다.");
        }
    }

    /**
     * 스케줄이 구역의 규칙에 맞게 생성되었는지 확인하는 메소드
     * @return 스케줄이 구역의 규칙에 맞게 생성된 경우 true, 아닌 경우 false
     */
    public boolean validateIsScheduleByAreaRules(LocalDate date, String areaName, String schoolClassName) {
        CleanArea cleanArea = getCleanAreaByNameAndClassName(areaName, schoolClassName);
        if (!cleanArea.getDays().contains(date.getDayOfWeek())) {
            return false;
        }
        LocalDate dateForWeekday = getDateForWeekday(date, date.getDayOfWeek());
        while (date.isBefore(dateForWeekday)) {
            date = date.plusWeeks(cleanArea.getCycle());
            if (date.equals(dateForWeekday)) {
                return true;
            }
        }
        return false;
    }

    private LocalDate getDateForWeekday(LocalDate date, DayOfWeek targetDay) {
        // 기준 날짜가 속한 주의 월요일을 구합니다.
        LocalDate monday = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        // 월요일로부터 targetDay까지의 일수 차이를 구한 후 더합니다.
        int daysToAdd = targetDay.getValue() - DayOfWeek.MONDAY.getValue();
        return monday.plusDays(daysToAdd);
    }

}
