package com.example.fingerprint_backend.controller;

import com.example.fingerprint_backend.ApiResponse;
import com.example.fingerprint_backend.dto.clean.SchoolClassRequest;
import com.example.fingerprint_backend.entity.SchoolClass;
import com.example.fingerprint_backend.service.CleanManagementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/admin")
public class AdminController {

    private final CleanManagementService cleanManagementService;

    public AdminController(CleanManagementService cleanManagementService) {
        this.cleanManagementService = cleanManagementService;
    }

    @PostMapping("/class")
    public ResponseEntity<ApiResponse> createClass(@RequestBody SchoolClassRequest request) {
        String className = request.getClassName();
        SchoolClass schoolClass = cleanManagementService.createSchoolClass(className);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "반 생성 성공", schoolClass));
    }
}
