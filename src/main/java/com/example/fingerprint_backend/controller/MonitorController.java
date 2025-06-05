package com.example.fingerprint_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "Monitoring", description = "모니터링 API / モニタリングAPI")
@RestController
@RequestMapping("/api/monitor")
public class MonitorController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${app.version:unknown}")
    private String appVersion;

    @Operation(operationId = "fullStatus", summary = "전체 상태 조회 / 全体状態確認", description = "서버의 전체 상태를 조회합니다。/ サーバーの全体状態を確認します。")
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> fullStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("time", LocalDateTime.now());
        status.put("version", appVersion);
        status.put("db", dbCheck());
        return ResponseEntity.ok(status);
    }

    @Operation(operationId = "healthCheck", summary = "헬스 체크 / ヘルスチェック", description = "서버의 헬스 상태를 확인합니다。/ サーバーのヘルス状態を確認します。")
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("OK");
    }

    @Operation(operationId = "serverTime", summary = "서버 시간 조회 / サーバー時間確認", description = "현재 서버 시간을 조회합니다。/ 現在のサーバー時間を確認します。")
    @GetMapping("/time")
    public ResponseEntity<String> serverTime() {
        return ResponseEntity.ok(LocalDateTime.now().toString());
    }

    @Operation(operationId = "info", summary = "서버 정보 조회 / サーバー情報確認", description = "서버의 정보를 조회합니다。/ サーバーの情報を確認します。")
    @GetMapping("/info")
    public ResponseEntity<Map<String, String>> info() {
        Map<String, String> info = new HashMap<>();
        info.put("version", appVersion);
        info.put("status", "UP");
        return ResponseEntity.ok(info);
    }

    @Operation(operationId = "dbCheck", summary = "DB 상태 확인 / DB状態確認", description = "데이터베이스의 상태를 확인합니다。/ データベースの状態を確認します。")
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
