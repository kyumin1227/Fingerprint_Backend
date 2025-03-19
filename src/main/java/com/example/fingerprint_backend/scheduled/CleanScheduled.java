package com.example.fingerprint_backend.scheduled;

import com.example.fingerprint_backend.entity.CleanArea;
import com.example.fingerprint_backend.entity.CleanGroup;
import com.example.fingerprint_backend.entity.CleanSchedule;
import com.example.fingerprint_backend.repository.CleanAreaRepository;
import com.example.fingerprint_backend.service.CleanOperationService;
import com.example.fingerprint_backend.service.CleanScheduleGroupService;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class CleanScheduled {

    private final CleanAreaRepository cleanAreaRepository;
    private final CleanScheduleGroupService cleanScheduleGroupService;
    private final CleanOperationService cleanOperationService;

    public CleanScheduled(CleanAreaRepository cleanAreaRepository, CleanScheduleGroupService cleanScheduleGroupService, CleanOperationService cleanOperationService) {
        this.cleanAreaRepository = cleanAreaRepository;
        this.cleanScheduleGroupService = cleanScheduleGroupService;
        this.cleanOperationService = cleanOperationService;
    }

    @Scheduled(cron = "0 0 12 * * ?")
    @Transactional
    /**
     * 자동으로 청소 스케줄을 완료하는 메소드
     */
    public void completeScheduleIfNeeded() {
        LocalDate today = LocalDate.now();
        int minusDays = 0;

        List<CleanSchedule> scheduleList = cleanScheduleGroupService.getScheduleByDateAndIsCanceled(today.minusDays(minusDays), false, false);

        for (CleanSchedule schedule : scheduleList) {
            cleanOperationService.completeCleaningSchedule(schedule.getDate(), schedule.getCleanArea().getName(), schedule.getSchoolClass().getId());
        }
    }

    @Scheduled(cron = "0 5 12 * * ?")
    @Transactional
    /**
     * 자동으로 청소 스케줄을 생성하는 메소드
     */
    public void createScheduleIfNeeded() {
        LocalDate today = LocalDate.now();
        List<CleanArea> areas = cleanAreaRepository.findAll();

        for (CleanArea cleanArea : areas) {
            List<CleanSchedule> schedules = cleanScheduleGroupService.getScheduleByAreaNameAndSchoolClassId(cleanArea.getName(), cleanArea.getSchoolClass().getId(), today);
            if (schedules.size() < cleanArea.getDisplay()) {
                cleanScheduleGroupService.createCleanSchedules(
                        cleanArea.getLastScheduledDate(),
                        cleanArea.getName(),
                        cleanArea.getSchoolClass().getId(),
                        cleanArea.getCycle(),
                        cleanArea.getDays(),
                        cleanArea.getDisplay() - schedules.size()
                );
            }
        }
    }

    @Scheduled(cron = "0 10 12 * * ?")
    @Transactional
    /**
     * 자동으로 청소 그룹을 생성하는 메소드
     */
    public void createGroupIfNeeded() {
        List<CleanArea> areas = cleanAreaRepository.findAll();

        for (CleanArea cleanArea : areas) {
            List<CleanGroup> groups = cleanScheduleGroupService.getGroupsByAreaNameAndClassIdAndIsCleaned(cleanArea.getName(), cleanArea.getSchoolClass().getId(), false);
            while (groups.size() < cleanArea.getDisplay()) {
                cleanScheduleGroupService.createGroupsByRandom(
                        cleanArea.getName(),
                        cleanArea.getSchoolClass().getId(),
                        cleanArea.getMembers(),
                        cleanArea.getGroupSize()
                );
            }
        }
    }

}
