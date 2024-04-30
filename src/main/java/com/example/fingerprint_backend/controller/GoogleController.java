package com.example.fingerprint_backend.controller;

import com.example.fingerprint_backend.dto.GoogleLoginDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GoogleController {

    @PostMapping("/login")
    public ResponseEntity<Boolean> login(@RequestBody GoogleLoginDto googleLoginDto) {
        String credential = googleLoginDto.getCredential();
        System.out.println("credential = " + credential);
        return ResponseEntity.status(HttpStatus.OK).body(true);
    }
}
