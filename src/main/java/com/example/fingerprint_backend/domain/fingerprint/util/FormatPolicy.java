package com.example.fingerprint_backend.domain.fingerprint.util;

import java.time.LocalTime;

public class FormatPolicy {

    /**
     * 시간을 ms로 받아 시:분:초로 포맷합니다.
     *
     * @param ms milliseconds
     * @return HH:mm:ss
     */
    public static String formatTime(long ms) {
        long seconds = ms / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        seconds %= 60;
        minutes %= 60;

        return String.format("%02d시간%02d분", hours, minutes);
    }

    /**
     * 시간을 ms로 받아 시:분:초로 포맷합니다.
     *
     * @param time LocalTime
     * @return HH:mm:ss
     */
    public static String formatTime(LocalTime time) {

        return String.format("%02d시%02d분", time.getHour(), time.getMinute());
    }

    public static String formatDate(long ms) {
        java.util.Date date = new java.util.Date(ms);
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }
}
