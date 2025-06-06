package com.example.fingerprint_backend.domain.fingerprint.service;

import com.example.fingerprint_backend.domain.fingerprint.entity.FingerPrintEntity;
import com.example.fingerprint_backend.dto.CreateFingerPrintDto;
import com.example.fingerprint_backend.entity.*;
import com.example.fingerprint_backend.domain.fingerprint.repository.FingerPrintRepository;
import com.example.fingerprint_backend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FingerPrintService {

    private final FingerPrintRepository fingerPrintRepository;
    private final MemberRepository memberRepository;

    /**
     * 학번의 가입 여부 확인
     * @param stdNum
     * @return
     */
    public Boolean isMemberExist(String stdNum) {

        Optional<MemberEntity> byStudentNumber = memberRepository.findByStudentNumber(stdNum);

        if (byStudentNumber.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 학번의 지문 정보 존재 여부 확인
     * @param stdNum
     * @return
     */
    public Boolean isFingerPrintExist(String stdNum) {

        Optional<FingerPrintEntity> byId = fingerPrintRepository.findById(stdNum);

        if (byId.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 지문 등록
     * @param createFingerPrintDto
     * @return
     */
    public FingerPrintEntity create(CreateFingerPrintDto createFingerPrintDto) {
        FingerPrintEntity fingerPrintEntity = new FingerPrintEntity(
                createFingerPrintDto.getStd_num(),
                createFingerPrintDto.getFingerprint1(),
                createFingerPrintDto.getFingerprint2(),
                LocalDateTime.now(),
                createFingerPrintDto.getSalt());

        FingerPrintEntity saved = fingerPrintRepository.save(fingerPrintEntity);

        return saved;
    }

    /**
     * 지문 삭제
     * @param stdNum
     * @return
     */
    public Boolean delete(String stdNum) {
        Optional<FingerPrintEntity> byId = fingerPrintRepository.findById(stdNum);

        if (byId.isEmpty()) {
            return false;
        }

        fingerPrintRepository.deleteById(stdNum);

        return true;
    }

    /**
     * 모든 지문 정보 반환 (라즈베리파이 최초 기동 시 사용)
     * @return
     */
    public List<FingerPrintEntity> getAllFingerprint() {

        List<FingerPrintEntity> all = fingerPrintRepository.findAll();

        return all;
    }

}
