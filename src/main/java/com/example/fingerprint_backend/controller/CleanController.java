package com.example.fingerprint_backend.controller;

import com.example.fingerprint_backend.ApiResult;
import com.example.fingerprint_backend.dto.clean.InfoResponse;
import com.example.fingerprint_backend.service.CleanHelperService;
import com.example.fingerprint_backend.service.CleanManagementService;
import com.example.fingerprint_backend.service.CleanOperationService;
import com.example.fingerprint_backend.service.CleanScheduleGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<ApiResult> getCleanInfo(@RequestParam("classId") Long classId,
                                                  @RequestParam("areaName") String areaName) {
        List<InfoResponse> cleanInfos = cleanOperationService.getCleanInfos(classId, areaName);

        return ResponseEntity.ok(new ApiResult(true, "청소 정보 조회 성공", cleanInfos));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResult> getCleanInfo(@RequestParam("classId") Long classId) {
        List<InfoResponse> cleanInfos = cleanOperationService.getCleanInfos(classId);

        return ResponseEntity.ok(new ApiResult(true, "청소 정보 조회 성공", cleanInfos));
    }

}
