package com.example.fingerprint_backend.service.Member;

import com.example.fingerprint_backend.entity.SchoolClass;
import com.example.fingerprint_backend.repository.SchoolClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ClassQueryService {

    private final SchoolClassRepository schoolClassRepository;

    public SchoolClass getClassById(Long classId) {

        return schoolClassRepository.findSchoolClassById(classId)
                .orElseThrow(() -> new IllegalArgumentException("해당 반이 존재하지 않습니다."));
    }
}
