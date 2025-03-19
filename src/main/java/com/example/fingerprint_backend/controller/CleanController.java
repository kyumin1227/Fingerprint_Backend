package com.example.fingerprint_backend.controller;

import com.example.fingerprint_backend.ApiResponse;
import com.example.fingerprint_backend.dto.clean.InfoResponse;
import com.example.fingerprint_backend.entity.CleanArea;
import com.example.fingerprint_backend.entity.CleanGroup;
import com.example.fingerprint_backend.entity.CleanSchedule;
import com.example.fingerprint_backend.jwt.CustomUserDetails;
import com.example.fingerprint_backend.service.CleanHelperService;
import com.example.fingerprint_backend.service.CleanManagementService;
import com.example.fingerprint_backend.service.CleanOperationService;
import com.example.fingerprint_backend.service.CleanScheduleGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/clean")
@RequiredArgsConstructor
public class CleanController {

    private final CleanManagementService cleanManagementService;
    private final CleanScheduleGroupService cleanScheduleGroupService;
    private final CleanOperationService cleanOperationService;
    private final CleanHelperService cleanHelperService;

    @GetMapping("")
    public ResponseEntity<ApiResponse> getCleanInfo(@RequestParam("classId") Long classId,
                                                    @RequestParam("areaName") String areaName) {
        List<CleanGroup> groups = cleanScheduleGroupService.getGroupsByAreaNameAndClassIdAndIsCleaned(areaName, classId, false);
        List<CleanSchedule> schedules = cleanScheduleGroupService.getScheduleByAreaNameAndSchoolClassId(areaName, classId, LocalDate.now());
        List<InfoResponse> infoResponses = cleanOperationService.parsingInfos(groups, schedules);

        return ResponseEntity.ok(new ApiResponse(true, "청소 정보 조회 성공", infoResponses));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getCleanInfo(@RequestParam("classId") Long classId) {
        List<CleanArea> cleanAreas = cleanHelperService.getCleanAreasBySchoolClassId(classId);
        List<InfoResponse> infoResponses = new ArrayList<>();
        cleanAreas.forEach(cleanArea -> {
            List<CleanGroup> groups = cleanScheduleGroupService.getGroupsByAreaNameAndClassIdAndIsCleaned(cleanArea.getName(), classId, false);
            List<CleanSchedule> schedules = cleanScheduleGroupService.getScheduleByAreaNameAndSchoolClassId(cleanArea.getName(), classId, LocalDate.now());
            try {
                cleanOperationService.appendParsingInfos(groups, schedules, infoResponses);
            } catch (IllegalStateException e) {
            }
        });

        List<InfoResponse> sortedInfoResponses = cleanOperationService.sortInfoResponses(infoResponses);

        return ResponseEntity.ok(new ApiResponse(true, "청소 정보 조회 성공", sortedInfoResponses));
    }

}
