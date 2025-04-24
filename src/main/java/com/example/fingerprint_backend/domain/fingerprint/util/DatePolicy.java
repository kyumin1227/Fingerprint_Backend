package com.example.fingerprint_backend.domain.fingerprint.util;

import lombok.experimental.UtilityClass;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

@UtilityClass
public class DatePolicy {

    /**
     * 해당 날짜의 특정 요일 반환
     *
     * @param date 기준 날짜
     * @return LocalDate (date의 주 시작일)
     */
    public static LocalDate getDateOfWeekDay(LocalDate date, DayOfWeek dayOfWeek) {
        return date.with(dayOfWeek);
    }

    /**
     * 해당 날짜의 특정 요일 반환 (TimePolicy 적용)
     *
     * @param dateTime 기준 날짜
     * @return LocalDate (dateTime의 주 시작일)
     */
    public static LocalDate getDateOfWeekDay(LocalDateTime dateTime, DayOfWeek dayOfWeek) {
        LocalDate date = TimePolicy.getLocalDate(dateTime);
        return getDateOfWeekDay(date, dayOfWeek);
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

    /**
     * 해당 날짜의 말일 반환
     *
     * @param date 기준 날짜
     * @return LocalDate (date의 월 마지막 날)
     */
    public static LocalDate getMonthEndDate(LocalDate date) {
        return date.withDayOfMonth(date.lengthOfMonth());
    }

    /**
     * 해당 날짜의 말일 반환 (TimePolicy 적용)
     *
     * @param dateTime 기준 날짜
     * @return LocalDate (dateTime의 월 마지막 날)
     */
    public static LocalDate getMonthEndDate(LocalDateTime dateTime) {
        LocalDate date = TimePolicy.getLocalDate(dateTime);
        return getMonthEndDate(date);
    }

    /**
     * 늦은 날짜 반환
     *
     * @param dateTime1 비교 날짜
     * @param dateTime2 비교 날짜
     * @return LocalDateTime (더 늦은 날짜)
     */
    public static LocalDateTime max(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        return dateTime1.isAfter(dateTime2) ? dateTime1 : dateTime2;
    }

    /**
     * 빠른 날짜 반환
     *
     * @param dateTime1 비교 날짜
     * @param dateTime2 비교 날짜
     * @return LocalDateTime (더 빠른 날짜)
     */
    public static LocalDateTime min(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        return dateTime1.isBefore(dateTime2) ? dateTime1 : dateTime2;
    }
}
