package com.example.fingerprint_backend;

import com.example.fingerprint_backend.entity.CleanSchedule;
import com.example.fingerprint_backend.service.CleanManagementService;
import com.example.fingerprint_backend.service.CleanScheduleService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest
@ContextConfiguration(initializers = DotenvTestInitializer.class)
@Transactional
public class CleanScheduleServiceTest {

    @Autowired private CleanScheduleService cleanScheduleService;
    @Autowired private CleanManagementService cleanManagementService;


    @BeforeEach
    void setUp() {
        cleanManagementService.createArea("창조관 405호");
        cleanManagementService.createClassroom("2027_A");
    }

    @DisplayName("청소 스케줄을 생성한다.")
    @Transactional
    @Test
    void create() {
        LocalDate date = LocalDate.now();
        CleanSchedule cleanSchedule = cleanScheduleService.create(date, "창조관 405호", "2027_A");

        assertThat(cleanSchedule).isNotNull();
    }

    @DisplayName("청소 스케줄을 가져온다.")
    @Transactional
    @Test
    void getCleanSchedule() {
        LocalDate date = LocalDate.now();
        CleanSchedule cleanSchedule = cleanScheduleService.create(date, "창조관 405호", "2027_A");

        assertThat(cleanSchedule).isEqualTo(cleanScheduleService.getCleanSchedule(date, "창조관 405호", "2027_A"));
    }

    @DisplayName("존재하지 않는 청소 스케쥴 요청.")
    @Transactional
    @Test
    void getCleanScheduleError() {
        LocalDate date = LocalDate.now();
        cleanScheduleService.create(date, "창조관 405호", "2027_A");
        cleanManagementService.createClassroom("2027_B");

        assertThatCode(() -> cleanScheduleService.getCleanSchedule(date, "창조관 405호", "2027_B"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 청소 스케쥴이 존재하지 않습니다.");
    }

    @DisplayName("청소 스케줄을 취소 후 다시 생성 한다.")
    @Transactional
    @Test
    void cancelCleanSchedule() {
        LocalDate date = LocalDate.now();
        CleanSchedule cleanSchedule = cleanScheduleService.create(date, "창조관 405호", "2027_A");

        cleanScheduleService.cancelCleanSchedule(date, "창조관 405호", "2027_A");

        assertThatCode(() -> cleanScheduleService.getCleanSchedule(date, "창조관 405호", "2027_A"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("취소된 청소 스케쥴입니다.");

        cleanSchedule = cleanScheduleService.create(date, "창조관 405호", "2027_A");

        assertThat(cleanSchedule).isNotNull();
    }

    @DisplayName("청소 스케줄을 변경 한다.")
    @Transactional
    @Test
    void changeCleanSchedule() {
        LocalDate date = LocalDate.now();
        CleanSchedule cleanSchedule = cleanScheduleService.create(date, "창조관 405호", "2027_A");

        CleanSchedule newCleanSchedule = cleanScheduleService.changeCleanSchedule(date, "창조관 405호", "2027_A", date.plusDays(1));

        assertThat(cleanSchedule.isCanceled()).as("취소된 청소 스케쥴").isTrue();
        assertThat(newCleanSchedule.isCanceled()).as("변경된 청소 스케쥴").isFalse();
    }

    @DisplayName("청소 스케줄을 변경 한다.")
    @Transactional
    @Test
    void changeCleanSchedule2() {
        LocalDate date = LocalDate.now();
        CleanSchedule cleanSchedule = cleanScheduleService.create(date, "창조관 405호", "2027_A");
        cleanScheduleService.create(date.plusDays(1), "창조관 405호", "2027_A");
        cleanScheduleService.cancelCleanSchedule(date.plusDays(1), "창조관 405호", "2027_A");

        CleanSchedule newCleanSchedule = cleanScheduleService.changeCleanSchedule(date, "창조관 405호", "2027_A", date.plusDays(1));

        assertThat(cleanSchedule.isCanceled()).as("취소된 청소 스케쥴").isTrue();
        assertThat(newCleanSchedule.isCanceled()).as("변경된 청소 스케쥴").isFalse();
    }

    @DisplayName("취소한 청소 스케줄을 변경 한다.")
    @Transactional
    @Test
    void changeCleanScheduleError() {
        LocalDate date = LocalDate.now();
        CleanSchedule cleanSchedule = cleanScheduleService.create(date, "창조관 405호", "2027_A");

        cleanScheduleService.cancelCleanSchedule(date, "창조관 405호", "2027_A");

        assertThatCode(() -> cleanScheduleService.changeCleanSchedule(date, "창조관 405호", "2027_A", date.plusDays(1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("취소된 청소 스케쥴입니다.");
    }

    @DisplayName("변경할 청소 스케줄이 이미 존재 하는 경우.")
    @Transactional
    @Test
    void changeCleanScheduleError2() {
        LocalDate date = LocalDate.now();
        CleanSchedule cleanSchedule = cleanScheduleService.create(date, "창조관 405호", "2027_A");
        CleanSchedule cleanSchedule2 = cleanScheduleService.create(date.plusDays(1), "창조관 405호", "2027_A");

        assertThatCode(() -> cleanScheduleService.changeCleanSchedule(date, "창조관 405호", "2027_A", date.plusDays(1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 청소 스케쥴이 이미 존재합니다, 스케쥴을 변경할 수 없습니다.");
    }

}
