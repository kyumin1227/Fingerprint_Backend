package com.example.fingerprint_backend.domain.fingerprint.controller;

import com.example.fingerprint_backend.ApiResponse;
import com.example.fingerprint_backend.domain.fingerprint.dto.RequestClassClose;
import com.example.fingerprint_backend.domain.fingerprint.service.classClosingTime.ClassClosingTimeApplicationService;
import com.example.fingerprint_backend.domain.fingerprint.service.log.LogApplicationService;
import com.example.fingerprint_backend.dto.CreateFingerPrintDto;
import com.example.fingerprint_backend.dto.CreateLogDto;
import com.example.fingerprint_backend.domain.fingerprint.entity.ClassClosingTime;
import com.example.fingerprint_backend.domain.fingerprint.entity.FingerPrintEntity;
import com.example.fingerprint_backend.domain.fingerprint.service.FingerPrintService;
import com.example.fingerprint_backend.domain.fingerprint.service.log.LogService;
import com.example.fingerprint_backend.service.Member.MemberQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class FingerPrintController {

    private final FingerPrintService fingerPrintService;
    private final LogService logService;
    private final MemberQueryService memberQueryService;
    private final LogApplicationService logApplicationService;
    private final ClassClosingTimeApplicationService classClosingTimeApplicationService;

    @GetMapping("/api/fingerprint/students/{stdNum}")
    public ResponseEntity<ApiResponse> check(@PathVariable String stdNum) {

        memberQueryService.getMemberByStudentNumber(stdNum);

//        해당 학번의 지문이 등록되어 있는지 조회
        Boolean fingerPrintExist = fingerPrintService.isFingerPrintExist(stdNum);

        if (fingerPrintExist) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(false, "이미 지문 정보가 등록된 학번입니다. \n 재등록은 관리자에게 문의 해주세요", null));
        }

        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "지문 등록이 가능한 학번입니다.", null));
    }

    @GetMapping("/api/fingerprint/students")
    public ResponseEntity<ApiResponse> getAllFingerprint(@RequestHeader HttpHeaders headers) {

//        String key = headers.getFirst("key");
//
//        if (key != "fweagiaewfsdgbneahpfasdfvweaghaegaew") {
//            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(false, "지문 정보를 가져올 권한이 없습니다.", null));
//        }

        List<FingerPrintEntity> allFingerprint = fingerPrintService.getAllFingerprint();

        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "모든 지문 정보를 가져왔습니다.", allFingerprint));

    }

    @PostMapping("/api/fingerprint/students")
    public ResponseEntity<ApiResponse> create(@RequestBody CreateFingerPrintDto createFingerPrintDto) {

        System.out.println("createFingerPrintDto.getFingerprint1() = " + createFingerPrintDto.getFingerprint1());
        System.out.println("createFingerPrintDto.getFingerprint2() = " + createFingerPrintDto.getFingerprint2());
        System.out.println("createFingerPrintDto.getStd_num() = " + createFingerPrintDto.getStd_num());

        FingerPrintEntity fingerPrintEntity = fingerPrintService.create(createFingerPrintDto);

        if (fingerPrintEntity.getStudentNumber().equals(createFingerPrintDto.getStd_num())) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "지문 등록에 성공하였습니다.", fingerPrintEntity));
        }

        return ResponseEntity.status(HttpStatus.OK).body((new ApiResponse(false, "지문 등록에 실패하였습니다. \n다시 시도해주세요.", null)));
    }

    @DeleteMapping("/api/fingerprint/students/{stdNum}")
    public ResponseEntity<ApiResponse> delete(@PathVariable String stdNum) {

        Boolean delete = fingerPrintService.delete(stdNum);

        if (!delete) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(false, "지문 데이터 삭제 실패", null));
        }

        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "지문 데이터 삭제 완료", null));
    }

    /**
     * 지문 인식 시 로그 생성 API
     *
     * @param createLogDto - 학번, 행동 (등교, 하교, 외출 등)
     * @throws IllegalArgumentException - 학번이 존재하지 않을 경우, 1분 이내 중복 로그 발생 시
     */
    @PostMapping("/api/fingerprint/logs")
    public ResponseEntity<ApiResponse> createLog(@RequestBody CreateLogDto createLogDto) {

//        LogEntity savedLog = logService.createLog(
//                createLogDto.getStd_num(),
//                createLogDto.getAction()
//        );
//
//        String message = String.format("학번 %s의 \n\"%s\" 로그가 등록되었습니다.", savedLog.getStudentNumber(), savedLog.getAction());

        logApplicationService.routeLog(
                createLogDto.getStd_num(),
                createLogDto.getAction(),
                LocalDateTime.now()
        );

//        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, message, savedLog));
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "로그 등록에 성공하였습니다.", null));
    }

    /**
     * 문 닫힘 API
     *
     * @param requestClassClose - 문 닫힘 담당자 학번
     * @throws IllegalArgumentException - 열쇠 담당자가 아닐 경우, 학번이 존재하지 않을 경우
     */
    @PostMapping("/api/fingerprint/close")
    public ResponseEntity<ApiResponse> close(@RequestBody RequestClassClose requestClassClose) {

        ClassClosingTime closingTime = classClosingTimeApplicationService.createClosingTime(LocalDateTime.now(), requestClassClose.closingMember());

        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "성공적으로 문을 닫았습니다.", closingTime));
    }
}
