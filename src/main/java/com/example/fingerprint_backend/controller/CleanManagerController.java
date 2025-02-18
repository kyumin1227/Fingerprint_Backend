package com.example.fingerprint_backend.controller;

import com.example.fingerprint_backend.ApiResponse;
import com.example.fingerprint_backend.dto.clean.*;
import com.example.fingerprint_backend.entity.CleanArea;
import com.example.fingerprint_backend.entity.CleanMember;
import com.example.fingerprint_backend.entity.SchoolClass;
import com.example.fingerprint_backend.service.CleanManagementService;
import com.example.fingerprint_backend.service.CleanScheduleGroupService;
import com.example.fingerprint_backend.types.CleanRole;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/clean/manager")
@RequiredArgsConstructor
public class CleanManagerController {

    private final CleanManagementService cleanManagementService;
    private final CleanScheduleGroupService cleanScheduleGroupService;

    @PostMapping("/class")
    public ResponseEntity<ApiResponse> createClass(@RequestBody SchoolClassRequest request) {
        String className = request.getClassName();
        SchoolClass schoolClass = cleanManagementService.createSchoolClass(className);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "반 생성 성공", schoolClass));
    }

    @PostMapping("/members")
    public ResponseEntity<ApiResponse> createMember(@RequestBody MemberRequest request) {
        CleanRole cleanRole = cleanManagementService.parseRoleOrDefault(request.getRole());
        CleanMember member = cleanManagementService.createMember(
                request.getStudentNumber(),
                request.getFirstName(),
                request.getGivenName(),
                request.getClassName(),
                cleanRole
        );
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "학생 추가 성공", member));
    }

    @PostMapping("/areas")
    public ResponseEntity<ApiResponse> createArea(@RequestBody AreaRequest request) {
        Set<DayOfWeek> days = request.getDaysOfWeek().stream().map(DayOfWeek::valueOf).collect(Collectors.toSet());
        CleanArea area = cleanManagementService.createArea(
                request.getAreaName(),
                request.getClassName(),
                days,
                request.getCycle()
        );
        if (request.getIsDefault() != null && request.getIsDefault()) {
            cleanManagementService.setDefaultArea(request.getAreaName(), request.getClassName());
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "청소 구역 생성 성공", area));
    }

    /**
     * 스케줄을 생성하는 컨트롤러
     */
    @PostMapping("/schedules")
    public ResponseEntity<ApiResponse> createSchedule(@RequestBody ScheduleRequest request) {
        cleanScheduleGroupService.createCleanSchedule(
                request.getDate(),
                request.getAreaName(),
                request.getClassName()
        );

        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "청소 스케줄 생성 성공", null));
    }

    /**
     * 자동으로 스케줄을 생성하는 컨트롤러 (주기, 요일, 갯수)
     */
    @PostMapping("/schedules/auto")
    public ResponseEntity<ApiResponse> createScheduleAuto(@RequestBody ScheduleAutoRequest request) {
        Set<DayOfWeek> days = request.getDaysOfWeek().stream().map(DayOfWeek::valueOf).collect(Collectors.toSet());
        cleanScheduleGroupService.createCleanSchedules(
                request.getDate(),
                request.getAreaName(),
                request.getClassName(),
                request.getCycle(),
                days,
                request.getCount()
        );
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "청소 스케줄 생성 성공", null));
    }

    /**
     * 랜덤으로 그룹을 생성하는 컨트롤러
     */
    @PostMapping("/groups/random")
    public ResponseEntity<ApiResponse> createGroupsByRandom(@RequestBody GroupRequest request) {
        List<CleanMember> members = cleanManagementService.getMembersBySchoolClassNameAndAreaName(
                request.getAreaName(),
                request.getClassName()
        );
        cleanScheduleGroupService.createGroupsByRandom(
                request.getAreaName(),
                request.getClassName(),
                members,
                (double) request.getGroupSize()
        );
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "그룹 생성 성공", null));
    }
}
