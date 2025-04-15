package com.example.fingerprint_backend.service;

import com.example.fingerprint_backend.dto.clean.InfoResponse;
import com.example.fingerprint_backend.entity.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class CleanOperationService {

    private final CleanHelperService cleanHelperService;
    private final CleanScheduleGroupService cleanScheduleGroupService;

    @Autowired
    public CleanOperationService(CleanHelperService cleanHelperService, CleanScheduleGroupService cleanScheduleGroupService) {
        this.cleanHelperService = cleanHelperService;
        this.cleanScheduleGroupService = cleanScheduleGroupService;
    }

    public List<InfoResponse> getCleanInfos(Long classId) {
        List<CleanArea> cleanAreas = cleanHelperService.getCleanAreasBySchoolClassId(classId);
        List<InfoResponse> cleanInfos = new ArrayList<>();

        cleanAreas.forEach(cleanArea -> {
            try {
                List<InfoResponse> infos = getCleanInfos(classId, cleanArea.getName());
                cleanInfos.addAll(infos);
            } catch (IllegalStateException e) {
                // ignore
            }
        });

        return sortInfoResponses(cleanInfos);
    }

    public List<InfoResponse> getCleanInfos(Long classId, String areaName) {
        List<CleanGroup> groups = cleanScheduleGroupService.getGroupsByAreaNameAndClassIdAndIsCleaned(areaName, classId, false);
        List<CleanSchedule> schedules = cleanScheduleGroupService.getScheduleByAreaNameAndSchoolClassId(areaName, classId, LocalDate.now());
        return parsingInfos(groups, schedules);
    }

    /**
     * 스케줄과 그룹을 합쳐 청소 정보를 파싱하는 메소드
     *
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
     *
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
     * 특정 날짜와 구역, 반으로 청소를 완료 처리하는 메소드
     *
     * @return 완료 처리 된 CleanGroup
     */
    @Transactional
    public CleanRecord completeCleaning(LocalDate date, String areaName, Long schoolClassId) {
        CleanSchedule cleanSchedule = completeCleanSchedule(date, areaName, schoolClassId);
        CleanGroup cleanGroup = completeCleanGroup(areaName, schoolClassId);
        return cleanScheduleGroupService.createCleanRecord(cleanSchedule, cleanGroup);
    }

    /**
     * 청소 스케줄을 완료 처리 하는 메소드
     *
     * @return 완료 처리 된 CleanSchedule
     */
    public CleanSchedule completeCleanSchedule(LocalDate date, String areaName, Long schoolClassId) {
        cleanHelperService.validateDateIsNotFuture(date);
        CleanSchedule cleanSchedule = cleanHelperService.getCleanScheduleByDateAndAreaNameAndClassId(date, areaName, schoolClassId);
        cleanHelperService.validateScheduleComplete(cleanSchedule);
        cleanSchedule.completed();
        return cleanSchedule;
    }

    /**
     * 청소 그룹을 완료 처리 하는 메소드
     *
     * @return 완료 처리 된 CleanGroup
     */
    public CleanGroup completeCleanGroup(String areaName, Long schoolClassId) {
        CleanGroup cleanGroup = cleanScheduleGroupService.getFirstGroup(areaName, schoolClassId);
        cleanGroup.setCleaned(true);
        cleanGroup.getMembers().forEach(member -> {
            CleanCountPerArea cleanCount = cleanHelperService.getOrNewCleanCountPerArea(
                    member.getStudentNumber(),
                    cleanGroup.getCleanArea().getId()
            );
            cleanCount.increment();
        });
        return cleanGroup;
    }

    public List<InfoResponse> sortInfoResponses(List<InfoResponse> infoResponses) {
        return infoResponses.stream()
                .sorted((o1, o2) -> o1.getDate().compareTo(o2.getDate()))
                .toList();
    }
}
