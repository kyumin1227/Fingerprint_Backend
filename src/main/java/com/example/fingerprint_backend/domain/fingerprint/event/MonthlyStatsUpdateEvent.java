package com.example.fingerprint_backend.domain.fingerprint.event;

import java.time.LocalDate;

public record MonthlyStatsUpdateEvent(

        String studentNumber,
        LocalDate effectiveDate

) {
}
