package com.example.fingerprint_backend.domain.fingerprint.service;

import com.example.fingerprint_backend.service.LogService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AttendanceCycleServiceTest {

    @Autowired
    private AttendanceCycleCommandService attendanceCycleCommandService;
    @Autowired
    private AttendanceCycleQueryService attendanceCycleQueryService;
    @Autowired
    private LogService logService;

    @DisplayName("정상적으로 등교 후 하교")
    @Test
    void success1() {
        // given
        attendanceCycleCommandService.create("2423002", LocalDateTime.of(2025, 4, 19, 22, 12));

        // when
        attendanceCycleCommandService.close("2423002", LocalDateTime.of(2025, 4, 19, 22, 22));
    }

    @DisplayName("등교 후 문 닫음 이후 10분 이내 하교")
    @Test
    void success2() {
        // given
        attendanceCycleCommandService.create("2423002", LocalDateTime.of(2025, 4, 19, 22, 12));

        // when
        logService.createClosingTime(LocalDateTime.of(2025, 4, 19, 22, 22), "2423002");
        attendanceCycleCommandService.close("2423002", LocalDateTime.of(2025, 4, 19, 22, 27));

        // then
        assertThat(attendanceCycleQueryService.getAttendanceCycle("2423002").getClosingTime()).isEqualTo(LocalDateTime.of(2025, 4, 19, 22, 27));

    }

    @DisplayName("등교 후 문 닫음 이후 재등교")
    @Test
    void success3() {

    }

    @DisplayName("등교 후 문 닫기 전 재등교 (이전 등교는 무시)")
    @Test
    void success4() {

    }

//    @DisplayName("등교 후 문 닫음 이후 재등교")
//    @Test
//    void error1() {
//
//    }

    @DisplayName("등교 후 문 닫음 이후 하교")
    @Test
    void error2() {

    }

    @DisplayName("등교 시간 보다 빠른 시간에 하교")
    @Test
    void error3() {

    }

    @DisplayName("등교 시간 보다 빠른 시간에 재등교")
    @Test
    void error4() {

    }

    @DisplayName("")
    @Test
    void error5() {

    }
}