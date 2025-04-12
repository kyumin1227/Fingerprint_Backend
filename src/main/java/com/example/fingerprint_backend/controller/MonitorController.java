package com.example.fingerprint_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/monitor")
public class MonitorController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${app.version:unknown}")
    private String appVersion;

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> fullStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("time", LocalDateTime.now());
        status.put("version", appVersion);
        status.put("db", dbCheck());
        return ResponseEntity.ok(status);
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/time")
    public ResponseEntity<String> serverTime() {
        return ResponseEntity.ok(LocalDateTime.now().toString());
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, String>> info() {
        Map<String, String> info = new HashMap<>();
        info.put("version", appVersion);
        info.put("status", "UP");
        return ResponseEntity.ok(info);
    }

    @GetMapping("/db")
    public ResponseEntity<String> dbCheck() {
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return ResponseEntity.ok("DB OK");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("DB ERROR");
        }
    }

}
