package com.example.fingerprint_backend.types;

import lombok.Getter;

import java.util.Set;

@Getter
public enum FileType {
    IMAGE(Set.of("jpg", "jpeg", "png", "webp", "gif")),
    DOCUMENT(Set.of("pdf", "docx", "hwp"));

    private final Set<String> extensions;

    FileType(Set<String> extensions) {
        this.extensions = extensions;
    }

}
