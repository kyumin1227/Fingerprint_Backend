package com.example.fingerprint_backend.migration;

import com.example.fingerprint_backend.domain.fingerprint.entity.ClassClosingTime;
import com.example.fingerprint_backend.domain.fingerprint.service.classClosingTime.ClassClosingTimeCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.time.LocalDateTime;

@ShellComponent
@RequiredArgsConstructor
public class FingerPrintMigration {

    private final ClassClosingTimeCommandService classClosingTimeCommandService;

    @ShellMethod("마이그레이션: 특정 날짜의 문 닫은 시간 등록")
    public String migrateClassClosingTime(
            @ShellOption(help = "추가할 문 닫은 시간 (예: 2025-04-27T22:55:00)",
                    value = {"-d", "--date"})
            LocalDateTime date,
            @ShellOption(help = "반 ID (예: 1)",
                    value = {"-c", "--class-id"})
            Long classId,
            @ShellOption(help = "문 닫은 학생 학번 (예: 2423002) 없으면 null",
                    value = {"-s", "--student-number"},
                    defaultValue = ShellOption.NULL)
            String studentNumber
    ) {

        ClassClosingTime closingTime = classClosingTimeCommandService.createClosingTime(date, classId, studentNumber);
        return closingTime.toString();
    }
}
