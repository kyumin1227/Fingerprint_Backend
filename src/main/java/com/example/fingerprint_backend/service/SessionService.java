package com.example.fingerprint_backend.service;

import com.example.fingerprint_backend.dto.DateInfoDto;
import com.example.fingerprint_backend.entity.DateEntity;
import com.example.fingerprint_backend.entity.MemberEntity;
import com.example.fingerprint_backend.repository.DateRepository;
import com.example.fingerprint_backend.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final DateRepository dateRepository;
    private final MemberRepository memberRepository;

//    오늘 날짜로부터 일주일의 날짜를 받아오는 서비스
    public ArrayList<LocalDate> getDateList() {

        ArrayList<LocalDate> dateArrayList = new ArrayList<>();

        LocalDate now = LocalDate.now();
        dateArrayList.add(now);

        for (int i = 1; i < 7; i++) {
            dateArrayList.add(now.plusDays(i));
        }

        return dateArrayList;
    }

//    일주일의 상세 정보를 받아오는 서비스
    public ArrayList<DateInfoDto> getDateInfos(ArrayList<LocalDate> dateArrayList, String stdNum) {

        ArrayList<DateInfoDto> dateInfoArrayList = new ArrayList<>();

        for (LocalDate date : dateArrayList) {
            Optional<DateEntity> dateInfo = dateRepository.findById(date);
            DateInfoDto dateInfoDto;
            if (dateInfo.isEmpty()) {
                dateInfoDto = new DateInfoDto(date, false, 0, false);
            } else {
                dateInfoDto = new DateInfoDto(date, dateInfo.get().getMembers().contains(stdNum), dateInfo.get().getMembers().size(), dateInfo.get().getIsHoliday());
            }
            dateInfoArrayList.add(dateInfoDto);
        }

        return dateInfoArrayList;
    }

    @Transactional
    public Boolean apply(LocalDate date, String stdNum) {
        Optional<DateEntity> targetDate = dateRepository.findById(date);
        Optional<MemberEntity> targetMember = memberRepository.findByStudentNumber(stdNum);

        DateEntity dateEntity = new DateEntity();

        if (targetMember.isEmpty()) {
            return false;
        }

        if (targetDate.isEmpty()) {
            DateEntity saveDate = new DateEntity();
            saveDate.setIsHoliday(false);
            saveDate.setDate(date);
            saveDate.getMembers().add(stdNum);
            dateRepository.save(saveDate);
        } else {
            targetDate.get().getMembers().add(stdNum);
            dateRepository.save(targetDate.get());
        }

        return true;
    }

    @Transactional
    public Boolean cancel(LocalDate date, String stdNum) {
        Optional<DateEntity> targetDate = dateRepository.findById(date);
        Optional<MemberEntity> targetMember = memberRepository.findByStudentNumber(stdNum);

        if (targetMember.isEmpty()) {
            return false;
        }

        if (targetDate.isEmpty()) {
            return false;
        } else {
            targetDate.get().getMembers().remove(stdNum);
            dateRepository.save(targetDate.get());
        }

        return true;
    }

}
