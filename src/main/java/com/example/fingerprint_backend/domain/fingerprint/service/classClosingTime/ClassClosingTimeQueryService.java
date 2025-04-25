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
     * 해당 시간 이후의 문 닫힘 시간을 반환하는 함수
     *
     * @param classId   반 ID
     * @param checkTime 확인할 시간
     * @return 해당 시간 이후의 문 닫힘 시간
     */
    public Optional<ClassClosingTime> getClassClosingTimeByTimeAfter(Long classId, LocalDateTime checkTime) {
        return classClosingTimeRepository.findBySchoolClassIdAndClosingTimeAfter(classId, checkTime);
    }

}
