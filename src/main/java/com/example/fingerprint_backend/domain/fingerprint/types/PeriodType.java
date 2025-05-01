package com.example.fingerprint_backend.domain.fingerprint.types;

import com.example.fingerprint_backend.domain.fingerprint.entity.BaseStats;
import com.example.fingerprint_backend.domain.fingerprint.entity.DailyStats;
import com.example.fingerprint_backend.domain.fingerprint.entity.MonthlyStats;
import com.example.fingerprint_backend.domain.fingerprint.entity.WeeklyStats;

public enum PeriodType {
    일간,
    주간,
    월간,
    전체;

    public Class<? extends BaseStats> getStats() {
        return switch (this) {
            case 일간:
                yield DailyStats.class;
            case 주간:
                yield WeeklyStats.class;
            case 월간:
                yield MonthlyStats.class;
            case 전체:
                yield null;
        };
    }
}
