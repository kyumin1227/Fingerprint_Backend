package com.example.fingerprint_backend.controller;

import com.example.fingerprint_backend.ApiResult;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Tag(name = "Clean Manager", description = "청소 관리 API / 清掃管理API<br>MANAGER 권한이 필요합니다。/ MANAGER権限が必要です。")
@RestController
@RequestMapping("/api/clean/manager")
@RequiredArgsConstructor
public class CleanManagerController {

    private final CleanManagementService cleanManagementService;
    private final CleanScheduleGroupService cleanScheduleGroupService;
    private final CleanOperationService cleanOperationService;
    private final CleanHelperService cleanHelperService;
    private final CleanScheduled cleanScheduled;

    @Operation(operationId = "createMember", summary = "학생 추가 / 学生追加", description = "청소 구역에 학생을 추가합니다。/ 清掃区域に学生を追加します。")
    @PostMapping("/members")
    public ResponseEntity<ApiResult> createMember(@AuthenticationPrincipal CustomUserDetails user,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "학생 추가 요청 본문 / 学生追加リクエストボディ<br><br>" +
                    "• `studentNumber` - 학번 / 学籍番号<br>" +
                    "• `givenName` - 이름 / 名前<br>" +
                    "• `familyName` - 성 / 姓<br>" +
                    "• `areaName` - 청소 구역 이름 / 清掃区域の名前") @RequestBody MemberRequest request) {
        CleanArea cleanArea = null;
        if (request.getAreaName() != null) {
            cleanArea = cleanHelperService.getCleanAreaByNameAndClassId(request.getAreaName(), user.getClassId());
        }
        CleanMember member = cleanManagementService.createMember(
                request.getStudentNumber(),
                request.getGivenName(),
                request.getFamilyName(),
                user.getClassId(),
                cleanArea);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResult(true, "학생 추가 성공", member));
    }

    @Operation(operationId = "updateMember", summary = "학생 수정 / 学生修正", description = "청소 구역의 학생 정보를 수정합니다。/ 清掃区域の学生情報を修正します。")
    @PatchMapping("/members/{studentNumber}")
    public ResponseEntity<ApiResult> updateMember(@AuthenticationPrincipal CustomUserDetails user,
            @PathVariable String studentNumber,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "학생 수정 요청 본문 / 学生修正リクエストボディ<br><br>" +
                    "• `givenName` - 이름 / 名前<br>" +
                    "• `familyName` - 성 / 姓<br>" +
                    "• `areaName` - 청소 구역 이름 / 清掃区域の名前") @RequestBody MemberRequest request) {
        cleanHelperService.validateCleanMemberInSchoolClass(user.getClassId(), studentNumber);
        cleanHelperService.validateCleanMemberExistsByStudentNumber(studentNumber);
        CleanMember member = cleanManagementService.updateMember(
                studentNumber,
                request.getGivenName(),
                request.getFamilyName(),
                request.getAreaName());
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResult(true, "학생 수정 성공", member));
    }

    @Operation(operationId = "deleteMember", summary = "학생 삭제 / 学生削除", description = "청소 구역에서 학생을 삭제합니다。/ 清掃区域から学生を削除します。")
    @DeleteMapping("/members/{studentNumber}")
    public ResponseEntity<ApiResult> deleteMember(@AuthenticationPrincipal CustomUserDetails user,
            @PathVariable String studentNumber) {
        cleanHelperService.validateCleanMemberInSchoolClass(user.getClassId(), studentNumber);
        cleanHelperService.validateCleanMemberExistsByStudentNumber(studentNumber);
        cleanManagementService.deleteMember(studentNumber);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResult(true, "학생 삭제 성공", null));
    }

    @Operation(operationId = "createArea", summary = "청소 구역 생성 / 清掃区域生成", description = "새로운 청소 구역을 생성합니다。/ 新しい清掃区域を生成します。")
    @PostMapping("/areas")
    @Transactional
    public ResponseEntity<ApiResult> createArea(@AuthenticationPrincipal CustomUserDetails user,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "청소 구역 생성 요청 본문 / 清掃区域生成リクエストボディ<br><br>"
                    +
                    "• `areaName` - 청소 구역 이름 / 清掃区域の名前<br>" +
                    "• `daysOfWeek` - 청소 요일 / 清掃曜日<br>" +
                    "• `cycle` - 청소 주기 / 清掃周期<br>" +
                    "• `isDefault` - 반의 기본 구역 여부 / クラスのデフォルトの区域かどうか<br>" +
                    "• `startDate` - 시작 날짜 / 開始日") @RequestBody AreaRequest request) {
        CleanArea area = cleanManagementService.createArea(
                request.getAreaName(),
                user.getClassId(),
                request.getDaysOfWeek(),
                request.getCycle());
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
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResult(true, "청소 구역 생성 성공", area));
    }

    /**
     * 스케줄을 생성하는 컨트롤러
     */
    @Operation(operationId = "createSchedule", summary = "청소 스케줄 생성 / 清掃スケジュール生成", description = "청소 스케줄을 생성합니다。/ 清掃スケジュールを生成します。")
    @PostMapping("/schedules")
    public ResponseEntity<ApiResult> createSchedule(@AuthenticationPrincipal CustomUserDetails user,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "청소 스케줄 생성 요청 본문 / 清掃スケジュール生成リクエストボディ<br><br>"
                    +
                    "• `date` - 청소 스케줄 날짜 / 清掃スケジュールの日付<br>" +
                    "• `areaName` - 청소 구역 이름 / 清掃区域の名前") @RequestBody ScheduleRequest request) {
        cleanScheduleGroupService.createAndRestoreCleanSchedule(
                request.getDate(),
                request.getAreaName(),
                user.getClassId());

        return ResponseEntity.status(HttpStatus.OK).body(new ApiResult(true, "청소 스케줄 생성 성공", null));
    }

    /**
     * 자동으로 스케줄을 생성하는 컨트롤러 (주기, 요일, 갯수)
     */
    @Operation(operationId = "createScheduleAuto", summary = "자동 청소 스케줄 생성 / 自動清掃スケジュール生成", description = "자동으로 청소 스케줄을 생성합니다。/ 自動で清掃スケジュールを生成します。")
    @PostMapping("/schedules/auto")
    public ResponseEntity<ApiResult> createScheduleAuto(@AuthenticationPrincipal CustomUserDetails user,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "자동 청소 스케줄 생성 요청 본문 / 自動清掃スケジュール生成リクエストボディ<br><br>"
                    +
                    "• `date` - 청소 스케줄 날짜 / 清掃スケジュールの日付<br>" +
                    "• `areaName` - 청소 구역 이름 / 清掃区域の名前<br>" +
                    "• `cycle` - 주기 / 周期<br>" +
                    "• `daysOfWeek` - 요일 / 曜日<br>" +
                    "• `count` - 갯수 / 個数") @RequestBody ScheduleAutoRequest request) {
        Set<DayOfWeek> days = request.getDaysOfWeek().stream().map(DayOfWeek::valueOf).collect(Collectors.toSet());
        cleanScheduleGroupService.createCleanSchedules(
                request.getDate(),
                request.getAreaName(),
                user.getClassId(),
                request.getCycle(),
                days,
                request.getCount());
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResult(true, "청소 스케줄 생성 성공", null));
    }

    /**
     * 랜덤으로 그룹을 생성하는 컨트롤러
     */
    @Operation(operationId = "createGroupsByRandom", summary = "랜덤 그룹 생성 / ランダムグループ生成", description = "랜덤으로 청소 그룹을 생성합니다。/ ランダムで清掃グループを生成します。")
    @PostMapping("/groups/random")
    public ResponseEntity<ApiResult> createGroupsByRandom(@AuthenticationPrincipal CustomUserDetails user,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "랜덤 그룹 생성 요청 본문 / ランダムグループ生成リクエストボディ<br><br>"
                    +
                    "• `areaName` - 청소 구역 이름 / 清掃区域の名前<br>" +
                    "• `groupSize` - 그룹 당 인원수 / グループ当たりの人数") @RequestBody GroupRequest request) {
        List<CleanMember> members = cleanManagementService.getMembersByAreaNameAndClassId(
                request.getAreaName(),
                user.getClassId());
        cleanScheduleGroupService.createGroupsByRandom(
                request.getAreaName(),
                user.getClassId(),
                members,
                (double) request.getGroupSize());
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResult(true, "그룹 생성 성공", null));
    }

    // TODO 그룹이 반에 속하는지 확인 필요
    // TODO 학생이 반에 속하는지 확인 로직 함수화

    /**
     * 그룹에 학생을 추가하는 컨트롤러
     */
    @Operation(operationId = "addMembersToGroup", summary = "그룹에 학생 추가 / グループに学生追加", description = "그룹에 학생을 추가합니다。/ グループに学生を追加します。")
    @PostMapping("/groups/{groupId}/members")
    public ResponseEntity<ApiResult> addMembersToGroup(@AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long groupId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "그룹에 학생 추가 요청 본문 / グループに学生追加リクエストボディ<br><br>"
                    +
                    "• `studentNumber` - 학번 / 学籍番号") @RequestBody GroupMemberRequest request) {

        CleanMember member = cleanHelperService.getCleanMemberByStudentNumber(request.getStudentNumber());

        if (!member.getSchoolClass().getId().equals(user.getClassId())) {
            throw new IllegalStateException("해당 학생은 해당 반에 속해있지 않습니다.");
        }

        cleanScheduleGroupService.appendMemberToGroup(groupId, request.getStudentNumber());
        CleanGroup group = cleanHelperService.getCleanGroupById(groupId);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResult(true, "그룹에 학생 추가 성공", group));
    }

    /**
     * 그룹에 학생을 제거하는 컨트롤러
     */
    @Operation(operationId = "removeMembersToGroup", summary = "그룹에서 학생 삭제 / グループから学生削除", description = "그룹에서 학생을 삭제합니다。/ グループから学生を削除します。")
    @DeleteMapping("/groups/{groupId}/members")
    public ResponseEntity<ApiResult> removeMembersToGroup(@AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long groupId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "그룹에 학생 삭제 요청 본문 / グループから学生削除リクエストボディ<br><br>"
                    +
                    "• `studentNumber` - 학번 / 学籍番号") @RequestBody GroupMemberRequest request) {

        CleanMember member = cleanHelperService.getCleanMemberByStudentNumber(request.getStudentNumber());

        if (!member.getSchoolClass().getId().equals(user.getClassId())) {
            throw new IllegalStateException("해당 학생은 해당 반에 속해있지 않습니다.");
        }

        cleanScheduleGroupService.removeMemberFromGroup(groupId, member.getStudentNumber());
        CleanGroup group = cleanHelperService.getCleanGroupById(groupId);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResult(true, "그룹에 학생 삭제 성공", group));
    }

    /**
     * 그룹의 학생을 교환하는 컨트롤러
     */
    @Operation(operationId = "swapMembersInGroup", summary = "그룹의 학생 교환 / グループの学生交換", description = "그룹의 학생을 교환합니다。/ グループの学生を交換します。")
    @Transactional
    @PutMapping("/groups/swap")
    public ResponseEntity<ApiResult> swapMembersInGroup(@AuthenticationPrincipal CustomUserDetails user,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "그룹의 학생 교환 요청 본문 / グループの学生交換リクエストボディ<br><br>"
                    +
                    "• `originStudentNumber` - 원본 학생 학번 / 元の学生の学籍番号<br>" +
                    "• `targetStudentNumber` - 대상 학생 학번 / 対象の学生の学籍番号<br>" +
                    "• `originGroupId` - 원본 그룹 ID / 元のグループID<br>" +
                    "• `targetGroupId` - 대상 그룹 ID / 対象のグループID") @RequestBody SwapMembersRequest request) {

        CleanMember originMember = cleanHelperService.getCleanMemberByStudentNumber(request.getOriginStudentNumber());
        CleanMember targetMember = cleanHelperService.getCleanMemberByStudentNumber(request.getTargetStudentNumber());

        if (!originMember.getSchoolClass().getId().equals(user.getClassId())
                || !targetMember.getSchoolClass().getId().equals(user.getClassId())) {
            throw new IllegalStateException("해당 학생은 해당 반에 속해있지 않습니다.");
        }

        cleanScheduleGroupService.removeMemberFromGroup(request.getOriginGroupId(), originMember.getStudentNumber());
        cleanScheduleGroupService.removeMemberFromGroup(request.getTargetGroupId(), targetMember.getStudentNumber());
        cleanScheduleGroupService.appendMemberToGroup(request.getOriginGroupId(), targetMember.getStudentNumber());
        cleanScheduleGroupService.appendMemberToGroup(request.getTargetGroupId(), originMember.getStudentNumber());

        CleanGroup originGroup = cleanHelperService.getCleanGroupById(request.getOriginGroupId());
        CleanGroup targetGroup = cleanHelperService.getCleanGroupById(request.getTargetGroupId());

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResult(true, "그룹의 학생 교환 성공", new CleanGroup[] { originGroup, targetGroup }));
    }

    /**
     * 청소 스케줄을 완료 처리하는 컨트롤러
     */
    @Operation(operationId = "completeCleaningSchedule", summary = "청소 스케줄 완료 / 清掃スケジュール完了", description = "청소 스케줄을 완료 처리합니다。/ 清掃スケジュールを完了処理します。")
    @PostMapping("/complete")
    public ResponseEntity<ApiResult> completeCleaningSchedule(@AuthenticationPrincipal CustomUserDetails user,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "청소 스케줄 완료 요청 본문 / 清掃スケジュール完了リクエストボディ<br><br>"
                    +
                    "• `date` - 청소 스케줄 날짜 / 清掃スケジュールの日付<br>" +
                    "• `areaName` - 청소 구역 이름 / 清掃区域の名前") @RequestBody ScheduleRequest request) {
        CleanRecord cleanRecord = cleanOperationService.completeCleaning(
                request.getDate(),
                request.getAreaName(),
                user.getClassId());
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResult(true, "청소 완료 처리 성공", cleanRecord.getCleanGroup()));
    }
}
