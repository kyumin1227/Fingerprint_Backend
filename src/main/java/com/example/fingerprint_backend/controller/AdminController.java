package com.example.fingerprint_backend.controller;

import com.example.fingerprint_backend.ApiResult;
import com.example.fingerprint_backend.dto.clean.SchoolClassRequest;
import com.example.fingerprint_backend.entity.SchoolClass;
import com.example.fingerprint_backend.service.CleanManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Admin", description = "관리자 API / 管理者API<br>Admin 권한이 필요합니다。/ 管理者権限が必要です。")
@RestController("/api/admin")
public class AdminController {

    private final CleanManagementService cleanManagementService;

    public AdminController(CleanManagementService cleanManagementService) {
        this.cleanManagementService = cleanManagementService;
    }

    @Operation(operationId = "createClass", summary = "반 생성 / クラス生成", description = "새로운 반이 시스템을 도입할 때 반을 추가하는 API입니다。<br>"
            +
            "新しいクラスがシステムを導入する際にクラスを追加するAPIです。")
    @PostMapping("/class")
    public ResponseEntity<ApiResult> createClass(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "반 생성 요청 본문 / クラス生成リクエストボディ<br><br>" +
                    "• `className` - 생성할 반의 이름 / 生成するクラスの名前") @RequestBody SchoolClassRequest request) {
        String className = request.getClassName();
        SchoolClass schoolClass = cleanManagementService.createSchoolClass(className);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResult(true, "반 생성 성공", schoolClass));
    }
}
