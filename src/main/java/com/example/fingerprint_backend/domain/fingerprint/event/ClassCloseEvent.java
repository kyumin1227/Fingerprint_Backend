package com.example.fingerprint_backend.domain.fingerprint.event;

import com.example.fingerprint_backend.domain.fingerprint.entity.ClassClosingTime;

public record ClassCloseEvent(
        ClassClosingTime classClosingTime
) {
}
