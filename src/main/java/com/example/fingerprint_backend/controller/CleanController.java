package com.example.fingerprint_backend.controller;

import com.example.fingerprint_backend.ApiResponse;
import com.example.fingerprint_backend.dto.clean.InfoResponse;
import com.example.fingerprint_backend.entity.CleanGroup;
import com.example.fingerprint_backend.entity.CleanSchedule;
import com.example.fingerprint_backend.jwt.CustomUserDetails;
import com.example.fingerprint_backend.service.CleanManagementService;
import com.example.fingerprint_backend.service.CleanOperationService;
import com.example.fingerprint_backend.service.CleanScheduleGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/clean")
@RequiredArgsConstructor
public class CleanController {

    private final CleanManagementService cleanManagementService;
    private final CleanScheduleGroupService cleanScheduleGroupService;
    private final CleanOperationService cleanOperationService;

    @GetMapping("")
    public ResponseEntity<ApiResponse> getCleanInfo(@AuthenticationPrincipal CustomUserDetails user,
                                                    @RequestParam("areaName") String areaName) {
        List<CleanGroup> groups = cleanScheduleGroupService.getGroupsByAreaNameAndClassIdAndIsCleaned(areaName, user.getClassId(), false);
        List<CleanSchedule> schedules = cleanScheduleGroupService.getScheduleByAreaNameAndSchoolClassId(areaName, user.getClassId(), LocalDate.now());
        List<InfoResponse> infoResponses = cleanOperationService.parsingInfos(groups, schedules);

        return ResponseEntity.ok(new ApiResponse(true, "청소 정보 조회 성공", infoResponses));
    }

}
