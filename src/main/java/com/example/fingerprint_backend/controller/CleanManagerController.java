package com.example.fingerprint_backend.controller;

import com.example.fingerprint_backend.ApiResponse;
import com.example.fingerprint_backend.dto.clean.*;
import com.example.fingerprint_backend.entity.*;
import com.example.fingerprint_backend.jwt.CustomUserDetails;
import com.example.fingerprint_backend.scheduled.CleanScheduled;
import com.example.fingerprint_backend.service.CleanHelperService;
import com.example.fingerprint_backend.service.CleanManagementService;
import com.example.fingerprint_backend.service.CleanOperationService;
import com.example.fingerprint_backend.service.CleanScheduleGroupService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
    private final CleanOperationService cleanOperationService;
    private final CleanHelperService cleanHelperService;
    private final CleanScheduled cleanScheduled;

    @PostMapping("/members")
    public ResponseEntity<ApiResponse> createMember(@AuthenticationPrincipal CustomUserDetails user,
                                                    @RequestBody MemberRequest request) {
        CleanArea cleanArea = null;
        if (request.getAreaName() != null) {
            cleanArea = cleanHelperService.getCleanAreaByNameAndClassId(request.getAreaName(), user.getClassId());
        }
        CleanMember member = cleanManagementService.createMember(
                request.getStudentNumber(),
                request.getGivenName(),
                request.getFamilyName(),
                user.getClassId(),
                cleanArea
        );
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "학생 추가 성공", member));
    }

    @PatchMapping("/members/{studentNumber}")
    public ResponseEntity<ApiResponse> updateMember(@AuthenticationPrincipal CustomUserDetails user,
                                                    @PathVariable String studentNumber,
                                                    @RequestBody MemberRequest request) {
        cleanHelperService.validateCleanMemberInSchoolClass(user.getClassId(), studentNumber);
        cleanHelperService.validateCleanMemberExistsByStudentNumber(studentNumber);
        CleanMember member = cleanManagementService.updateMember(
                studentNumber,
                request.getGivenName(),
                request.getFamilyName(),
                request.getAreaName()
        );
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "학생 수정 성공", member));
    }

    @DeleteMapping("/members/{studentNumber}")
    public ResponseEntity<ApiResponse> deleteMember(@AuthenticationPrincipal CustomUserDetails user,
                                                    @PathVariable String studentNumber) {
        cleanHelperService.validateCleanMemberInSchoolClass(user.getClassId(), studentNumber);
        cleanHelperService.validateCleanMemberExistsByStudentNumber(studentNumber);
        cleanManagementService.deleteMember(studentNumber);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "학생 삭제 성공", null));
    }

    @PostMapping("/areas")
    @Transactional
    public ResponseEntity<ApiResponse> createArea(@AuthenticationPrincipal CustomUserDetails user,
                                                  @RequestBody AreaRequest request) {
        CleanArea area = cleanManagementService.createArea(
                request.getAreaName(),
                user.getClassId(),
                request.getDaysOfWeek(),
                request.getCycle()
        );
        if (request.getIsDefault()) {
            cleanManagementService.setDefaultArea(request.getAreaName(), user.getClassId());
        }
        if (request.getStartDate() != null) {
            area.setLastScheduledDate(request.getStartDate());
        }
        if (request.getDisplay() != null) {
            area.setDisplay(request.getDisplay());
        }
        if (request.getGroupSize() != null) {
            area.setGroupSize(request.getGroupSize());
        }
        cleanScheduled.createScheduleIfNeeded();
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "청소 구역 생성 성공", area));
    }

    /**
     * 스케줄을 생성하는 컨트롤러
     */
    @PostMapping("/schedules")
    public ResponseEntity<ApiResponse> createSchedule(@AuthenticationPrincipal CustomUserDetails user,
                                                      @RequestBody ScheduleRequest request) {
        cleanScheduleGroupService.createAndRestoreCleanSchedule(
                request.getDate(),
                request.getAreaName(),
                user.getClassId()
        );

        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "청소 스케줄 생성 성공", null));
    }

    /**
     * 자동으로 스케줄을 생성하는 컨트롤러 (주기, 요일, 갯수)
     */
    @PostMapping("/schedules/auto")
    public ResponseEntity<ApiResponse> createScheduleAuto(@AuthenticationPrincipal CustomUserDetails user,
                                                          @RequestBody ScheduleAutoRequest request) {
        Set<DayOfWeek> days = request.getDaysOfWeek().stream().map(DayOfWeek::valueOf).collect(Collectors.toSet());
        cleanScheduleGroupService.createCleanSchedules(
                request.getDate(),
                request.getAreaName(),
                user.getClassId(),
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
    public ResponseEntity<ApiResponse> createGroupsByRandom(@AuthenticationPrincipal CustomUserDetails user,
                                                            @RequestBody GroupRequest request) {
        List<CleanMember> members = cleanManagementService.getMembersByAreaNameAndClassId(
                request.getAreaName(),
                user.getClassId()
        );
        cleanScheduleGroupService.createGroupsByRandom(
                request.getAreaName(),
                user.getClassId(),
                members,
                (double) request.getGroupSize()
        );
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "그룹 생성 성공", null));
    }

//    TODO 그룹이 반에 속하는지 확인 필요
//    TODO 학생이 반에 속하는지 확인 로직 함수화

    /**
     * 그룹에 학생을 추가하는 컨트롤러
     */
    @PostMapping("/groups/{groupId}/members")
    public ResponseEntity<ApiResponse> addMembersToGroup(@AuthenticationPrincipal CustomUserDetails user,
                                                         @PathVariable Long groupId,
                                                         @RequestBody GroupMemberRequest request) {

        CleanMember member = cleanHelperService.getCleanMemberByStudentNumber(request.getStudentNumber());

        if (!member.getSchoolClass().getId().equals(user.getClassId())) {
            throw new IllegalStateException("해당 학생은 해당 반에 속해있지 않습니다.");
        }

        cleanScheduleGroupService.appendMemberToGroup(groupId, request.getStudentNumber());
        CleanGroup group = cleanHelperService.getCleanGroupById(groupId);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "그룹에 학생 추가 성공", group));
    }

    /**
     * 그룹에 학생을 제거하는 컨트롤러
     */
    @DeleteMapping("/groups/{groupId}/members")
    public ResponseEntity<ApiResponse> removeMembersToGroup(@AuthenticationPrincipal CustomUserDetails user,
                                                            @PathVariable Long groupId,
                                                            @RequestBody GroupMemberRequest request) {

        CleanMember member = cleanHelperService.getCleanMemberByStudentNumber(request.getStudentNumber());

        if (!member.getSchoolClass().getId().equals(user.getClassId())) {
            throw new IllegalStateException("해당 학생은 해당 반에 속해있지 않습니다.");
        }

        cleanScheduleGroupService.removeMemberFromGroup(groupId, member.getStudentNumber());
        CleanGroup group = cleanHelperService.getCleanGroupById(groupId);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "그룹에 학생 삭제 성공", group));
    }

    /**
     * 그룹의 학생을 교환하는 컨트롤러
     */
    @Transactional
    @PutMapping("/groups/swap")
    public ResponseEntity<ApiResponse> swapMembersInGroup(@AuthenticationPrincipal CustomUserDetails user,
                                                          @RequestBody SwapMembersRequest request) {

        CleanMember originMember = cleanHelperService.getCleanMemberByStudentNumber(request.getOriginStudentNumber());
        CleanMember targetMember = cleanHelperService.getCleanMemberByStudentNumber(request.getTargetStudentNumber());

        if (!originMember.getSchoolClass().getId().equals(user.getClassId()) || !targetMember.getSchoolClass().getId().equals(user.getClassId())) {
            throw new IllegalStateException("해당 학생은 해당 반에 속해있지 않습니다.");
        }

        cleanScheduleGroupService.removeMemberFromGroup(request.getOriginGroupId(), originMember.getStudentNumber());
        cleanScheduleGroupService.removeMemberFromGroup(request.getTargetGroupId(), targetMember.getStudentNumber());
        cleanScheduleGroupService.appendMemberToGroup(request.getOriginGroupId(), targetMember.getStudentNumber());
        cleanScheduleGroupService.appendMemberToGroup(request.getTargetGroupId(), originMember.getStudentNumber());

        CleanGroup originGroup = cleanHelperService.getCleanGroupById(request.getOriginGroupId());
        CleanGroup targetGroup = cleanHelperService.getCleanGroupById(request.getTargetGroupId());

        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "그룹의 학생 교환 성공", new CleanGroup[]{originGroup, targetGroup}));
    }

    /**
     * 청소 스케줄을 완료 처리하는 컨트롤러
     */
    @PostMapping("/complete")
    public ResponseEntity<ApiResponse> completeCleaningSchedule(@AuthenticationPrincipal CustomUserDetails user,
                                                                @RequestBody ScheduleRequest request) {
        CleanRecord cleanRecord = cleanOperationService.completeCleaning(
                request.getDate(),
                request.getAreaName(),
                user.getClassId()
        );
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "청소 완료 처리 성공", cleanRecord.getCleanGroup()));
    }
}
