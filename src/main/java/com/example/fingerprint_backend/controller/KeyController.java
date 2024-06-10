package com.example.fingerprint_backend.controller;

import com.example.fingerprint_backend.ApiResponse;
import com.example.fingerprint_backend.dto.KeyInfoDto;
import com.example.fingerprint_backend.dto.KeyInfoReturnDto;
import com.example.fingerprint_backend.entity.KeyEntity;
import com.example.fingerprint_backend.service.KeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class KeyController {

    private final KeyService keyService;

    @GetMapping("/api/key")
    public ResponseEntity<ApiResponse> getKeyInfo(@RequestParam(name = "date") String date) {
        System.out.println("date = " + date);

        KeyEntity keyInfo = keyService.getKeyInfo(date);

        KeyInfoReturnDto keyInfoReturnDto = keyService.setName(keyInfo);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse(true, "키 값 가져옴", keyInfoReturnDto));
    }

    @PostMapping("/api/key")
    public ResponseEntity<ApiResponse> setKeyInfo(@RequestBody KeyInfoDto keyInfoDto) {

        System.out.println("keyInfoDto.getDate() = " + keyInfoDto.getDate());
        System.out.println("keyInfoDto.getStudentNumber() = " + keyInfoDto.getKeyStudent());
        System.out.println("keyInfoDto.getStartTime() = " + keyInfoDto.getStartTime());
        System.out.println("keyInfoDto.getEndTime() = " + keyInfoDto.getEndTime());
        System.out.println("keyInfoDto.getAmendStudentNumber() = " + keyInfoDto.getAmendStudentNumber());

        KeyEntity keyEntity = keyService.setKeyInfo(keyInfoDto);

        KeyInfoReturnDto keyInfoReturnDto = keyService.setName(keyEntity);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse(true, "키 정보 변경", keyInfoReturnDto));
    }
}
