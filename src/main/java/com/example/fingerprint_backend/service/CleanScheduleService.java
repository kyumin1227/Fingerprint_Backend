package com.example.fingerprint_backend.service;

import com.example.fingerprint_backend.entity.Classroom;
import com.example.fingerprint_backend.entity.CleanArea;
import com.example.fingerprint_backend.entity.CleanGroup;
import com.example.fingerprint_backend.entity.CleanSchedule;
import com.example.fingerprint_backend.repository.CleanScheduleRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class CleanScheduleService {

    private final CleanScheduleRepository cleanScheduleRepository;
    private final CleanManagementService cleanManagementService;

    @Autowired
    public CleanScheduleService(CleanScheduleRepository cleanScheduleRepository, CleanManagementService cleanManagementService) {
        this.cleanScheduleRepository = cleanScheduleRepository;
        this.cleanManagementService = cleanManagementService;
    }

    /**
     * 청소 스케줄을 생성하는 메소드
     */
    public CleanSchedule create(LocalDate date, String cleanAreaName, String classroomName) {
        CleanArea area = cleanManagementService.getAreaByName(cleanAreaName);
        Classroom classroom = cleanManagementService.getClassroomByName(classroomName);
        CleanSchedule cleanSchedule = cleanScheduleRepository.getCleanScheduleByDateAndCleanAreaAndClassroom(date, area, classroom)
                .orElseGet(() -> new CleanSchedule(date, area, classroom));

        if (cleanSchedule.isCanceled()) {
            cleanSchedule.setCanceled(false);
        }

        return cleanScheduleRepository.save(cleanSchedule);
    }

    /**
     * 청소 스케줄을 가져오는 메소드
     */
    public CleanSchedule getCleanSchedule(LocalDate date, String cleanAreaName, String classroomName) {
        CleanArea area = cleanManagementService.getAreaByName(cleanAreaName);
        Classroom classroom = cleanManagementService.getClassroomByName(classroomName);

        CleanSchedule cleanSchedule = cleanScheduleRepository.getCleanScheduleByDateAndCleanAreaAndClassroom(date, area, classroom)
                .orElseThrow(() -> new IllegalArgumentException("해당 청소 스케쥴이 존재하지 않습니다."));

        if (cleanSchedule.isCanceled()) {
            throw new IllegalArgumentException("취소된 청소 스케쥴입니다.");
        }

        return cleanSchedule;
    }

    /**
     * 청소 스케줄을 취소하는 메소드
     */
    public void cancelCleanSchedule(LocalDate date, String cleanAreaName, String classroomName) {
        CleanSchedule cleanSchedule = getCleanSchedule(date, cleanAreaName, classroomName);
        cleanSchedule.setCanceled(true);
        cleanSchedule.setCleanGroup(null);
    }

    /**
     * 청소 스케줄을 변경하는 메소드
     */
    @Transactional
    public CleanSchedule changeCleanSchedule(LocalDate date, String cleanAreaName, String classroomName, LocalDate newDate) {
        CleanSchedule cleanSchedule = getCleanSchedule(date, cleanAreaName, classroomName);
        CleanGroup cleanGroup = cleanSchedule.getCleanGroup();

        CleanSchedule newCleanSchedule = cleanScheduleRepository.getCleanScheduleByDateAndCleanAreaAndClassroom(newDate, cleanSchedule.getCleanArea(), cleanSchedule.getClassroom())
                .map(s -> {
                    if (s.isCanceled()) {
                        s.setCanceled(false);
                        s.setCleanGroup(cleanGroup);
                        return s;
                    } else {
                        throw new IllegalArgumentException("해당 청소 스케쥴이 이미 존재합니다, 스케쥴을 변경할 수 없습니다.");
                    }
                })
                .orElseGet(() -> new CleanSchedule(newDate, cleanSchedule.getCleanArea(), cleanSchedule.getClassroom()));

        cleanSchedule.setCanceled(true);
        cleanSchedule.setCleanGroup(null);

        return cleanScheduleRepository.save(newCleanSchedule);
    }

    /**
     * 청소 그룹을 설정하는 메소드
     */
    public void setCleanGroup(LocalDate date, String cleanAreaName, String classroomName, CleanGroup cleanGroup) {
        CleanSchedule cleanSchedule = getCleanSchedule(date, cleanAreaName, classroomName);
        cleanSchedule.setCleanGroup(cleanGroup);
    }

    /**
     * 청소 그룹을 가져오는 메소드
     */
    public CleanGroup getCleanGroup(LocalDate date, String cleanAreaName, String classroomName) {
        CleanSchedule cleanSchedule = getCleanSchedule(date, cleanAreaName, classroomName);
        return cleanSchedule.getCleanGroup();
    }
}
