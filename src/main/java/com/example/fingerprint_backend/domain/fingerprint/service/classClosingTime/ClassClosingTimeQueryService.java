package com.example.fingerprint_backend.domain.fingerprint.service.classClosingTime;

import com.example.fingerprint_backend.domain.fingerprint.entity.ClassClosingTime;
import com.example.fingerprint_backend.domain.fingerprint.repository.ClassClosingTimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ClassClosingTimeQueryService {

    private final ClassClosingTimeRepository classClosingTimeRepository;

    /**
     * 시간 범위 내의 가장 빠른 문 닫힘 시간을 가져옵니다.
     *
     * @param classId   반 ID
     * @param startTime 시작 시간
     * @param endTime   종료 시간
     * @return 해당 시간 이후의 문 닫힘 시간
     */
    public Optional<ClassClosingTime> getClassClosingTimeByTimeRange(Long classId, LocalDateTime startTime, LocalDateTime endTime) {
        return classClosingTimeRepository.findTopBySchoolClassIdAndClosingTimeBetweenOrderByClosingTimeAsc(classId, startTime, endTime);
    }

}
