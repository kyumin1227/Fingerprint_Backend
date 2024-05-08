package com.example.fingerprint_backend.service;

import com.example.fingerprint_backend.dto.CreateFingerPrintDto;
import com.example.fingerprint_backend.dto.CreateLogDto;
import com.example.fingerprint_backend.entity.FingerPrintEntity;
import com.example.fingerprint_backend.entity.LogEntity;
import com.example.fingerprint_backend.entity.MemberEntity;
import com.example.fingerprint_backend.repository.FingerPrintRepository;
import com.example.fingerprint_backend.repository.LogRepository;
import com.example.fingerprint_backend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FingerPrintService {

    private final FingerPrintRepository fingerPrintRepository;
    private final LogRepository logRepository;
    private final MemberRepository memberRepository;

//    학번의 가입 여부 확인
    public Boolean isMemberExist(String stdNum) {

        Optional<MemberEntity> byStudentNumber = memberRepository.findByStudentNumber(stdNum);

        if (byStudentNumber.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

//    학번의 지문 정보 존재 여부 확인
    public Boolean isFingerPrintExist(String stdNum) {

        Optional<FingerPrintEntity> byId = fingerPrintRepository.findById(stdNum);

        if (byId.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

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

    public Boolean delete(String stdNum) {
        Optional<FingerPrintEntity> byId = fingerPrintRepository.findById(stdNum);

        if (byId.isEmpty()) {
            return false;
        }

        fingerPrintRepository.deleteById(stdNum);

        return true;
    }

    public List<FingerPrintEntity> getAllFingerprint() {

        List<FingerPrintEntity> all = fingerPrintRepository.findAll();

        return all;
    }

    public LogEntity createLog(CreateLogDto createLogDto) {

        LogEntity logEntity = new LogEntity();
        logEntity.setAction(createLogDto.getAction());
        logEntity.setStudentNumber(createLogDto.getStd_num());
        logEntity.setEventTime(LocalDateTime.now());

        LogEntity saved = logRepository.save(logEntity);

        return saved;
    }

}
