package com.example.fingerprint_backend.migration;

import com.example.fingerprint_backend.domain.fingerprint.entity.ClassClosingTime;
import com.example.fingerprint_backend.domain.fingerprint.entity.LogEntity;
import com.example.fingerprint_backend.domain.fingerprint.service.classClosingTime.ClassClosingTimeCommandService;
import com.example.fingerprint_backend.domain.fingerprint.service.cycle.CycleApplicationService;
import com.example.fingerprint_backend.domain.fingerprint.service.log.LogService;
import com.example.fingerprint_backend.domain.fingerprint.util.DatePolicy;
import com.example.fingerprint_backend.domain.fingerprint.util.TimePolicy;
import com.example.fingerprint_backend.types.LogAction;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@ShellComponent
@RequiredArgsConstructor
public class FingerPrintMigration {

    private final ClassClosingTimeCommandService classClosingTimeCommandService;
    private final LogService logService;
    private final CycleApplicationService cycleApplicationService;

    @ShellMethod("마이그레이션: 특정 날짜의 문 닫은 시간 등록")
    public String migrateClassClosingTime(
            @ShellOption(help = "추가할 문 닫은 시간 (예: 2025-04-27T22:55:00)",
                    value = {"-s", "--start-date"})
            LocalDateTime startDate,
            @ShellOption(help = "추가할 문 닫은 시간 (예: 2025-04-27T22:55:00)",
                    value = {"-e", "--end-date"})
            LocalDateTime endDate,
            @ShellOption(help = "반 ID (예: 1)",
                    value = {"-c", "--class-id"})
            Long classId
    ) {

        List<LogEntity> logs = logService.getLogsInRangeByAction(
                LogAction.하교,
                startDate,
                endDate
        );

        List<LogEntity> lastLeaveLogs = logs.stream()
                .collect(Collectors.groupingBy(log -> TimePolicy.getLocalDate(log.getEventTime())))
                .values().stream()
                .map(dayLogs -> dayLogs.stream()
                        .max(Comparator.comparing(LogEntity::getEventTime))
                        .orElseThrow())
                .collect(Collectors.toList());

        lastLeaveLogs.sort(Comparator.comparing(LogEntity::getEventTime));

        lastLeaveLogs
                .forEach(
                log -> {
                    classClosingTimeCommandService.createClosingTime(log.getEventTime(), classId, null);
                }
        );

        return "마이그레이션 완료" + lastLeaveLogs.size() + "개 문 닫은 시간 등록됨";
    }

    @ShellMethod("마이그레이션: 특정 날짜의 로그를 통해 사이클 생성")
    public String migrateCycle(
            @ShellOption(help = "추가할 로그 시작 시간 (예: 2025-04-27T22:55:00)",
                    value = {"-s", "--start-date"})
            LocalDateTime startDate,
            @ShellOption(help = "추가할 로그 종료 시간 (예: 2025-04-27T22:55:00)",
                    value = {"-e", "--end-date"})
            LocalDateTime endDate
    ) {

        List<LogEntity> logs = logService.getLogsInRange(startDate, endDate);

        logs.forEach(
                log -> {
                    if (log.getAction() == LogAction.등교) {
                        cycleApplicationService.createAttendanceCycle(log.getStudentNumber(), log.getEventTime());
                    } else if (log.getAction() == LogAction.하교) {
                        cycleApplicationService.closeAttendanceCycle(log.getStudentNumber(), log.getEventTime());
                    } else if (log.getAction() == LogAction.식사 || log.getAction() == LogAction.기타 || log.getAction() == LogAction.도서관) {
                        cycleApplicationService.createOutingCycle(log.getStudentNumber(), log.getEventTime(), log.getAction());
                    } else if (log.getAction() == LogAction.복귀) {
                        cycleApplicationService.closeOutingCycle(log.getStudentNumber(), log.getEventTime());
                    }

                    System.out.println("로그 처리 완료: " + log.getId());
                }
        );


        return "마이그레이션 완료" + logs.size() + "개 로그 처리됨";
    }

}
