package com.example.fingerprint_backend.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class TimePolicy {

    private static final LocalTime START_TIME = LocalTime.of(6, 0); // 06:00

    /**
     * 날짜 정책을 적용하여 LocalDateTime을 LocalDate로 변환합니다.
     *
     * @param localDateTime 변환할 LocalDateTime
     * @return 변환된 LocalDate
     */
    public static LocalDate getLocalDate(LocalDateTime localDateTime) {
        return localDateTime.toLocalTime().isBefore(START_TIME)
                ? localDateTime.toLocalDate().minusDays(1)
                : localDateTime.toLocalDate();
    }

    /**
     * 해당 날짜의 시작 시각 (06:00)
     *
     * @param date 기준 날짜
     * @return LocalDateTime (date 06:00)
     */
    public static LocalDateTime getStartDateTime(LocalDate date) {
        return date.atTime(START_TIME);
    }

    /**
     * 해당 날짜의 종료 시각 (다음날 06:00)
     *
     * @param date 기준 날짜
     * @return LocalDateTime (date + 1일 06:00)
     */
    public static LocalDateTime getEndDateTime(LocalDate date) {
        return date.plusDays(1).atTime(START_TIME);
    }

}
