package com.example.fingerprint_backend.service;

import com.example.fingerprint_backend.dto.DateInfoDto;
import com.example.fingerprint_backend.entity.DateEntity;
import com.example.fingerprint_backend.entity.MemberEntity;
import com.example.fingerprint_backend.repository.DateRepository;
import com.example.fingerprint_backend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
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
    public ArrayList<DateInfoDto> getDateInfo(ArrayList<LocalDate> dateArrayList, String stdNum) {

        ArrayList<DateInfoDto> dateInfoArrayList = new ArrayList<>();
        MemberEntity member = memberRepository.findByStudentNumber(stdNum).orElseThrow();

        for (LocalDate date : dateArrayList) {
            Optional<DateEntity> dateInfo = dateRepository.findByDate(date);
            DateInfoDto dateInfoDto;
            if (dateInfo.isEmpty()) {
                dateInfoDto = new DateInfoDto(date, false, 0, false);
            } else {
                dateInfoDto = new DateInfoDto(date, dateInfo.get().getMembers().contains(member), dateInfo.get().getMembers().size(), dateInfo.get().getIsHoliday());
            }
            dateInfoArrayList.add(dateInfoDto);
        }

        return dateInfoArrayList;
    }

}
