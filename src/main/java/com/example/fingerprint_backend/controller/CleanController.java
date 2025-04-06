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
        List<InfoResponse> cleanInfos = cleanOperationService.getCleanInfos(classId, areaName);

        return ResponseEntity.ok(new ApiResponse(true, "청소 정보 조회 성공", cleanInfos));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getCleanInfo(@RequestParam("classId") Long classId) {
        List<InfoResponse> cleanInfos = cleanOperationService.getCleanInfos(classId);

        return ResponseEntity.ok(new ApiResponse(true, "청소 정보 조회 성공", cleanInfos));
    }

}
