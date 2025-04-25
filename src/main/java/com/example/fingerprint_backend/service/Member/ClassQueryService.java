package com.example.fingerprint_backend.service.Member;

import com.example.fingerprint_backend.entity.SchoolClass;
import com.example.fingerprint_backend.repository.SchoolClassRepository;
import org.springframework.stereotype.Service;

@Service
public class ClassQueryService {

    private final SchoolClassRepository schoolClassRepository;

    public ClassQueryService(SchoolClassRepository schoolClassRepository) {
        this.schoolClassRepository = schoolClassRepository;
    }

    public SchoolClass getClassById(Long classId) {

        return schoolClassRepository.findSchoolClassById(classId)
                .orElseThrow(() -> new IllegalArgumentException("해당 반이 존재하지 않습니다."));
    }
}
