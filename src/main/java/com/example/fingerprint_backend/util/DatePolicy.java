package com.example.fingerprint_backend.util;

import lombok.experimental.UtilityClass;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

@UtilityClass
public class DatePolicy {

    /**
     * 해당 날짜의 월요일 반환
     *
     * @param date 기준 날짜
     * @return LocalDate (date의 주 시작일)
     */
    public static LocalDate getWeekStartDate(LocalDate date) {
        return date.with(DayOfWeek.MONDAY);
    }

    /**
     * 해당 날짜의 월요일 반환 (TimePolicy 적용)
     *
     * @param dateTime 기준 날짜
     * @return LocalDate (dateTime의 주 시작일)
     */
    public static LocalDate getWeekStartDate(LocalDateTime dateTime) {
        LocalDate date = TimePolicy.getLocalDate(dateTime);
        return getWeekStartDate(date);
    }

    /**
     * 해당 날짜의 1일 반환
     *
     * @param date 기준 날짜
     * @return LocalDate (date의 월 시작일)
     */
    public static LocalDate getMonthStartDate(LocalDate date) {
        return date.withDayOfMonth(1);
    }

    /**
     * 해당 날짜의 1일 반환 (TimePolicy 적용)
     *
     * @param dateTime 기준 날짜
     * @return LocalDate (dateTime의 월 시작일)
     */
    public static LocalDate getMonthStartDate(LocalDateTime dateTime) {
        LocalDate date = TimePolicy.getLocalDate(dateTime);
        return getMonthStartDate(date);
    }
}
