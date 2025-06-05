package com.example.fingerprint_backend.controller;

import com.example.fingerprint_backend.ApiResult;
import com.example.fingerprint_backend.dto.clean.InfoResponse;
import com.example.fingerprint_backend.service.CleanOperationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Clean Info", description = "청소 정보 API / 清掃情報API")
@RestController
@RequestMapping("/api/clean")
@RequiredArgsConstructor
public class CleanController {

    private final CleanOperationService cleanOperationService;

    @Operation(operationId = "getCleanInfoByArea", summary = "청소 정보 조회 (구역별) / 清掃情報取得 (区域別)", description = "특정 반의 특정 구역에 대한 청소 정보를 조회합니다。/ 特定のクラスの特定の区域に関する清掃情報を取得します。")
    @GetMapping("")
    public ResponseEntity<ApiResult> getCleanInfo(@RequestParam("classId") Long classId,
            @RequestParam("areaName") String areaName) {
        List<InfoResponse> cleanInfos = cleanOperationService.getCleanInfos(classId, areaName);

        return ResponseEntity.ok(new ApiResult(true, "청소 정보 조회 성공", cleanInfos));
    }

    @Operation(operationId = "getAllCleanInfo", summary = "모든 청소 정보 조회 / 全ての清掃情報取得", description = "특정 반에 대한 모든 청소 정보를 조회합니다。/ 特定のクラスに関する全ての清掃情報を取得します。")
    @GetMapping("/all")
    public ResponseEntity<ApiResult> getCleanInfo(@RequestParam("classId") Long classId) {
        List<InfoResponse> cleanInfos = cleanOperationService.getCleanInfos(classId);

        return ResponseEntity.ok(new ApiResult(true, "청소 정보 조회 성공", cleanInfos));
    }

}
