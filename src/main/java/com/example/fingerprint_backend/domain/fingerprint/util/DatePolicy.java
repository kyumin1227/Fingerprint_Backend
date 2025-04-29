package com.example.fingerprint_backend.domain.fingerprint.util;

import com.example.fingerprint_backend.domain.fingerprint.types.PeriodType;
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
     * @param first 비교 날짜
     * @param second 비교 날짜
     * @return LocalDateTime (더 늦은 날짜)
     */
    public static LocalDateTime max(LocalDateTime first, LocalDateTime second) {
        if (first == null) {
            return second;
        }
        if (second == null) {
            return first;
        }
        return first.isAfter(second) ? first : second;
    }

    /**
     * 빠른 날짜 반환
     *
     * @param first 비교 날짜
     * @param second 비교 날짜
     * @return LocalDateTime (더 빠른 날짜)
     */
    public static LocalDateTime min(LocalDateTime first, LocalDateTime second) {
        if (first == null) {
            return second;
        }
        if (second == null) {
            return first;
        }
        return first.isBefore(second) ? first : second;
    }

    /**
     * 기간 타입에 따라 해당하는 날짜를 반환합니다.
     *
     * @param date       기준 날짜
     * @param periodType 기간 타입
     * @return LocalDate (주어진 기간 타입에 따른 날짜)
     */
    public LocalDate getDateByPeriodType(LocalDate date, PeriodType periodType) {

        if (periodType == PeriodType.주간) {
            return getDateOfWeekDay(date, DayOfWeek.MONDAY);
        } else if (periodType == PeriodType.월간) {
            return getMonthStartDate(date);
        }
        return date;
    }
}
