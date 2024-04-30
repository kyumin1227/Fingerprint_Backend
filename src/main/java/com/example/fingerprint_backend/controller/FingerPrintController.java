package com.example.fingerprint_backend.controller;

import com.example.fingerprint_backend.dto.FingerPrintDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FingerPrintController {

    @PostMapping("/new")
    public ResponseEntity<Boolean> create(@RequestBody FingerPrintDto fingerPrintDto) {
        System.out.println(fingerPrintDto.toString());

        return ResponseEntity.status(HttpStatus.OK).body(true);
    }
}
