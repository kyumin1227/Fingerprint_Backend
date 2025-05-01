package com.example.fingerprint_backend.domain.fingerprint.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class TimePolicyTest {


    @DisplayName("정상적인 시간")
    @Test
    void getLocalTimeToSecond1() {
        // given
        LocalDateTime localDateTime = LocalDateTime.of(2025, 4, 26, 23, 22, 0);

        // when
        Long localTimeToSecond = TimePolicy.getLocalTimeToSecond(localDateTime);

        // then
        assertThat(LocalTime.ofSecondOfDay(localTimeToSecond)).isEqualTo(LocalTime.of(23, 22, 0));
    }

    @DisplayName("경계값 테스트")
    @Test
    void getLocalTimeToSecond2() {
        // given
        LocalDateTime localDateTime = LocalDateTime.of(2025, 4, 26, 5, 59, 59);

        // when
        Long localTimeToSecond = TimePolicy.getLocalTimeToSecond(localDateTime);

        // then
        assertThat(LocalTime.ofSecondOfDay(localTimeToSecond % 86400)).isEqualTo(LocalTime.of(5, 59, 59));

    }
}