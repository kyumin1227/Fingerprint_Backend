package com.example.fingerprint_backend.controller;

import com.example.fingerprint_backend.ApiResponse;
import com.example.fingerprint_backend.dto.DateInfoDto;
import com.example.fingerprint_backend.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;

@RestController
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    @GetMapping("/sessions")
    public ResponseEntity<ApiResponse> getDates(@RequestParam(name = "stdNum") String stdNum) {

        ArrayList<LocalDate> dateList = sessionService.getDateList();

        ArrayList<DateInfoDto> dateInfo = sessionService.getDateInfo(dateList, stdNum);

        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(false, "Fasd", dateInfo));
    }


}
