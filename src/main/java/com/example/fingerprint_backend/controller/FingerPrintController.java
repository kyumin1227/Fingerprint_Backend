package com.example.fingerprint_backend.controller;

import com.example.fingerprint_backend.ApiResponse;
import com.example.fingerprint_backend.dto.CreateFingerPrintDto;
import com.example.fingerprint_backend.dto.CreateLogDto;
import com.example.fingerprint_backend.entity.FingerPrintEntity;
import com.example.fingerprint_backend.entity.LogEntity;
import com.example.fingerprint_backend.service.FingerPrintService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FingerPrintController {

    private final FingerPrintService fingerPrintService;

    @GetMapping("fingerprint/students/{stdNum}")
    public ResponseEntity<ApiResponse> check(@PathVariable String stdNum) {

//        해당 학번의 유저가 가입되어 있는지 조회
        Boolean exist = fingerPrintService.isMemberExist(stdNum);

//        (스테이터스 코드 204 반환)
        if (!exist) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ApiResponse(false, "가입 되지 않은 학번입니다.", null));
        }

//        해당 학번의 지문이 등록되어 있는지 조회
        Boolean fingerPrintExist = fingerPrintService.isFingerPrintExist(stdNum);

        if (fingerPrintExist) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(false, "이미 지문 정보가 \n등록된 학번입니다.", null));
        }

        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "지문 등록이 가능한 학번입니다.", null));
    }

    @GetMapping("fingerprint/students")
    public ResponseEntity<ApiResponse> getAllFingerprint(@RequestHeader HttpHeaders headers) {

//        String key = headers.getFirst("key");
//
//        if (key != "fweagiaewfsdgbneahpfasdfvweaghaegaew") {
//            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(false, "지문 정보를 가져올 권한이 없습니다.", null));
//        }

        List<FingerPrintEntity> allFingerprint = fingerPrintService.getAllFingerprint();

        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "모든 지문 정보를 가져왔습니다.", allFingerprint));

    }

    @PostMapping("/fingerprint/students")
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

    @DeleteMapping("/fingerprint/students/{stdNum}")
    public ResponseEntity<ApiResponse> delete(@PathVariable String stdNum) {

        Boolean delete = fingerPrintService.delete(stdNum);

        if (!delete) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(false, "지문 데이터 삭제 실패", null));
        }

        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "지문 데이터 삭제 완료", null));
    }

    @PostMapping("/fingerprint/logs")
    public ResponseEntity<ApiResponse> createLog(@RequestBody CreateLogDto createLogDto) {

        System.out.println("createLogDto.getStd_num() = " + createLogDto.getStd_num());
        System.out.println("createLogDto.getAction() = " + createLogDto.getAction());
        LogEntity savedLog = fingerPrintService.createLog(createLogDto);

        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "로그 등록: 로그가 등록되었습니다", savedLog));
    }
}
