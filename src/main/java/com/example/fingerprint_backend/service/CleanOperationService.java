package com.example.fingerprint_backend.service;

import com.example.fingerprint_backend.dto.clean.InfoResponse;
import com.example.fingerprint_backend.entity.CleanGroup;
import com.example.fingerprint_backend.entity.CleanSchedule;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class CleanOperationService {

    private final CleanManagementService cleanManagementService;
    private final CleanHelperService cleanHelperService;
    private final CleanScheduleGroupService cleanScheduleGroupService;

    @Autowired
    public CleanOperationService(CleanManagementService cleanManagementService, CleanHelperService cleanHelperService, CleanScheduleGroupService cleanScheduleGroupService) {
        this.cleanManagementService = cleanManagementService;
        this.cleanHelperService = cleanHelperService;
        this.cleanScheduleGroupService = cleanScheduleGroupService;
    }

    /**
     * 스케줄과 그룹을 합쳐 청소 정보를 파싱하는 메소드
     * @return 파싱된 청소 정보 리스트
     */
    public List<InfoResponse> parsingInfos(List<CleanGroup> groups, List<CleanSchedule> schedules) {
        cleanHelperService.validateCleanScheduleNotEmpty(schedules);
        cleanHelperService.validateCleanGroupNotEmpty(groups);
        List<InfoResponse> infoResponses = new ArrayList<>();
        ArrayList<CleanGroup> groupsCopy = new ArrayList<>(groups);
        ArrayList<CleanSchedule> schedulesCopy = new ArrayList<>(schedules);
        for (CleanSchedule schedule : schedulesCopy) {
            if (schedule.isCanceled()) {
                infoResponses.add(new InfoResponse(schedule.getDate()));
            } else if (!groupsCopy.isEmpty()) {
                CleanGroup group = groupsCopy.remove(0);
                infoResponses.add(parsingInfo(group, schedule));
            }
        }
        return infoResponses;
    }

    /**
     * 그룹과 스케줄을 합쳐 청소 정보를 파싱하는 메소드
     * @return 파싱된 청소 정보
     */
    public InfoResponse parsingInfo(CleanGroup group, CleanSchedule schedule) {
        return new InfoResponse(
                schedule.getDate(),
                group.getId(),
                group.getMembers(),
                group.getMemberCount(),
                schedule.getCleanArea().getName(),
                schedule.getSchoolClass().getName(),
                false
        );
    }

    /**
     * 특정 날짜의 청소 스케줄을 완료하는 메소드
     * @return 완료 처리 된 CleanGroup
     */
    @Transactional
    public CleanGroup completeCleaningSchedule(LocalDate date, String areaName, Long schoolClassId) {
        cleanHelperService.validateDateIsNotFuture(date);
        CleanSchedule cleanSchedule = cleanHelperService.getCleanScheduleByDateAndAreaNameAndClassId(date, areaName, schoolClassId);
        cleanHelperService.validateScheduleComplete(cleanSchedule);
        CleanGroup cleanGroup = cleanScheduleGroupService.getFirstGroup(areaName, schoolClassId);
        cleanGroup.setCleaned(true);
        cleanSchedule.completed();
        return cleanGroup;
    }
}
