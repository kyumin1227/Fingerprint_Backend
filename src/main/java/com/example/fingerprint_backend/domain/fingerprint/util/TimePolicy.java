package com.example.fingerprint_backend.domain.fingerprint.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@UtilityClass
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
     * 날짜의 시간을 초 단위로 변환합니다. (하루의 시작 기준 보정)
     *
     * @param localDateTime 날짜
     * @return Long 보정된 시간 (초 단위)
     */
    public static Long getLocalTimeToSecond(LocalDateTime localDateTime) {

        LocalTime localTime = localDateTime.toLocalTime();

        return localTime.isBefore(START_TIME)
                ? (long) localTime.toSecondOfDay() + 86400
                : (long) localTime.toSecondOfDay();
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

    /**
     * 밀리초를 시:분 형식의 문자열로 변환합니다.
     *
     * @param millis 변환할 밀리초
     * @return 변환된 문자열 (예: "02시간 30분")
     */
    public static String convertMillisToTimeString(long millis) {
        long seconds = millis / 1000;
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;

        return String.format("%02d시간 %02d분", hours, minutes);
    }

}
