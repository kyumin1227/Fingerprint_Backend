package com.example.fingerprint_backend.controller;

import com.example.fingerprint_backend.ApiResponse;
import com.example.fingerprint_backend.dto.ApplyDto;
import com.example.fingerprint_backend.dto.DateInfoDto;
import com.example.fingerprint_backend.service.GoogleService;
import com.example.fingerprint_backend.service.SessionService;
import com.example.fingerprint_backend.types.MemberRole;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.util.ArrayList;

@RestController
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;
    private final GoogleService googleService;

    @GetMapping("/api/sessions")
    public ResponseEntity<ApiResponse> getDates(@RequestParam(name = "stdNum") String stdNum) {

        ArrayList<LocalDate> dateList = sessionService.getDateList();

        ArrayList<DateInfoDto> dateInfo = sessionService.getDateInfos(dateList, stdNum);

        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(false, "Fasd", dateInfo));
    }

    @PostMapping("/api/sessions/{date}")
    public ResponseEntity<ApiResponse> apply(@PathVariable String date, @RequestBody ApplyDto applyDto) throws GeneralSecurityException, IOException {

        LocalDate localDate = LocalDate.of(Integer.parseInt(date.substring(0, 4)), Integer.parseInt(date.substring(4, 6)), Integer.parseInt(date.substring(6)));

        System.out.println(applyDto.getCredential());
        if (!googleService.googleTokenCheck(applyDto.getCredential())) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(false, "신청 실패: 토큰이 유효하지 않습니다.", null));
        }

        System.out.println("localDate = " + localDate);

        DateInfoDto dateInfo;

//        이미 신청이 되어있는 상태 (sign = true)
        if (applyDto.getSign()) {
            if (sessionService.cancel(localDate, applyDto.getStudentNum(), applyDto.getRole())) {
                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "취소 성공: 취소가 완료되었습니다.", null));
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(false, "신청 실패: 새로고침 후 다시 시도해주세요.", null));
            }
        } else {
            if (sessionService.apply(localDate, applyDto.getStudentNum(), applyDto.getRole())) {
                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "신청 성공: 신청이 완료되었습니다.", null));
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(false, "신청 실패: 새로고침 후 다시 시도해주세요.", null));
            }

        }

    }
}
