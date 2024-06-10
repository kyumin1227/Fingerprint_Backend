package com.example.fingerprint_backend.service;

import com.example.fingerprint_backend.dto.KeyInfoDto;
import com.example.fingerprint_backend.dto.KeyInfoReturnDto;
import com.example.fingerprint_backend.entity.KeyEntity;
import com.example.fingerprint_backend.entity.MemberEntity;
import com.example.fingerprint_backend.repository.KeyRepository;
import com.example.fingerprint_backend.repository.MemberRepository;
import jakarta.servlet.http.PushBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KeyService {

    private final KeyRepository keyRepository;
    private final MemberRepository memberRepository;

    /**
     * 날짜를 보내서 해당 날짜의 키정보를 가져오는 서비스
     * @param date
     * @return
     */
    public KeyEntity getKeyInfo(String date) {

        LocalDate targetDate = LocalDate.parse(date);
        DayOfWeek dayOfWeek = targetDate.getDayOfWeek();

        System.out.println("targetDate = " + targetDate);

        Optional<KeyEntity> byId = keyRepository.findById(targetDate);

        if (byId.isEmpty()) {
            LocalTime start = LocalTime.parse("09:00");
            LocalTime end = LocalTime.parse("21:00");
            KeyEntity newKeyInfo = new KeyEntity(targetDate, null, null, start, end, null, null, dayOfWeek.equals(DayOfWeek.SATURDAY) || dayOfWeek.equals(DayOfWeek.SUNDAY));

            KeyEntity save = keyRepository.save(newKeyInfo);
            return save;
        }

        return byId.get();
    }

    /**
     * 전달받은 키정보를 입력하는 서비스
     * @param keyInfoDto
     * @return
     */
    public KeyEntity setKeyInfo(KeyInfoDto keyInfoDto) {

        Optional<KeyEntity> byId = keyRepository.findById(keyInfoDto.getDate());

        KeyEntity targetKeyEntity = byId.get();

        targetKeyEntity.setStartTime(keyInfoDto.getStartTime());
        targetKeyEntity.setEndTime(keyInfoDto.getEndTime());
        targetKeyEntity.setKeyStudent(keyInfoDto.getKeyStudent());
        targetKeyEntity.setSubManager(keyInfoDto.getSubManager());
        targetKeyEntity.setAmendDate(LocalDateTime.now());
        targetKeyEntity.setAmendStudentNumber(keyInfoDto.getAmendStudentNumber());
        targetKeyEntity.setIsHoliday(keyInfoDto.getIsHoliday());

        KeyEntity save = keyRepository.save(targetKeyEntity);

        return save;

    }

    public KeyInfoReturnDto setName(KeyEntity keyEntity) {

        KeyInfoReturnDto keyInfoReturnDto = new KeyInfoReturnDto();
        keyInfoReturnDto.setDate(keyEntity.getDate());
        keyInfoReturnDto.setStartTime(keyEntity.getStartTime());
        keyInfoReturnDto.setEndTime(keyEntity.getEndTime());
        keyInfoReturnDto.setIsHoliday(keyEntity.getIsHoliday());
        keyInfoReturnDto.setAmendDate(keyEntity.getAmendDate());

        Optional<MemberEntity> keyStudentMember = memberRepository.findByStudentNumber(keyEntity.getKeyStudent());
        Optional<MemberEntity> subManagerMember = memberRepository.findByStudentNumber(keyEntity.getSubManager());

        keyInfoReturnDto.setKeyStudent(keyEntity.getKeyStudent());

        if (keyStudentMember.isEmpty()) {
            keyInfoReturnDto.setKeyStudentName("찾을 수 없음");
        } else {
            keyInfoReturnDto.setKeyStudentName(keyStudentMember.get().getName());
        }

        keyInfoReturnDto.setSubManager(keyEntity.getSubManager());

        if (subManagerMember.isEmpty()) {
            keyInfoReturnDto.setSubManagerName("찾을 수 없음");
        } else {
            keyInfoReturnDto.setSubManagerName(subManagerMember.get().getName());
        }

        return keyInfoReturnDto;

    }

}
