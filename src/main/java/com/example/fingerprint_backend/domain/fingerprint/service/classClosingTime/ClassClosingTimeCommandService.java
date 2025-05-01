package com.example.fingerprint_backend.domain.fingerprint.service.classClosingTime;

import com.example.fingerprint_backend.domain.fingerprint.entity.ClassClosingTime;
import com.example.fingerprint_backend.domain.fingerprint.exception.LogException;
import com.example.fingerprint_backend.domain.fingerprint.repository.ClassClosingTimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class ClassClosingTimeCommandService {

    private final ClassClosingTimeRepository classClosingTimeRepository;

    /**
     * 문 닫힘 시간 등록
     *
     * @param closingMember 문 닫힘 담당자 학번
     * @param closingTime   문 닫힘 시간
     * @return 문 닫힘 시간 등록된 객체
     */
    public ClassClosingTime createClosingTime(LocalDateTime closingTime, Long classId, String closingMember) {

        ClassClosingTime classClosingTime = new ClassClosingTime(closingTime, classId, closingMember);

        return classClosingTimeRepository.save(classClosingTime);
    }

    /**
     * 문닫음 중복 확인
     *
     * @param classId     반 ID
     * @param closingTime 로그 발생 시간
     * @throws LogException 5분 이내에 문을 닫았을 경우
     */
    public void checkDuplicateClose(Long classId, LocalDateTime closingTime) {

        LocalDateTime checkTime = closingTime.minusMinutes(5);

        classClosingTimeRepository.findTopBySchoolClassIdAndClosingTimeBetweenOrderByClosingTimeAsc(classId, checkTime, closingTime)
                .ifPresent(log -> {
                    throw new LogException("이미 문이 닫혀있습니다.");
                });
    }
}
