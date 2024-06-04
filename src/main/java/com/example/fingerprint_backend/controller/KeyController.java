package com.example.fingerprint_backend.controller;

import com.example.fingerprint_backend.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KeyController {

    @GetMapping("/key/{date}")
    public ResponseEntity<ApiResponse> getKeyInfo(@RequestParam(name = "date") String date) {
        System.out.println("date = " + date);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse(true, "키 값 가져옴", null));
    }
}
