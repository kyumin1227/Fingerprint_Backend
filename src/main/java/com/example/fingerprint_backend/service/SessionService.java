package com.example.fingerprint_backend.service;

import com.example.fingerprint_backend.dto.DateInfoDto;
import com.example.fingerprint_backend.dto.UserListDto;
import com.example.fingerprint_backend.entity.DateEntity;
import com.example.fingerprint_backend.entity.KeyEntity;
import com.example.fingerprint_backend.entity.MemberEntity;
import com.example.fingerprint_backend.repository.DateRepository;
import com.example.fingerprint_backend.repository.KeyRepository;
import com.example.fingerprint_backend.repository.MemberRepository;
import com.example.fingerprint_backend.types.MemberRole;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final DateRepository dateRepository;
    private final MemberRepository memberRepository;
    private final KeyRepository keyRepository;

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
            Optional<KeyEntity> keyInfo = keyRepository.findById(date);
            Boolean isHoliday = keyInfo.isEmpty() ? false : keyInfo.get().getIsHoliday();
            DateInfoDto dateInfoDto;
            if (dateInfo.isEmpty()) {
                DateEntity dateEntity = new DateEntity(date, false, null);
                dateRepository.save(dateEntity);
                dateInfoDto = new DateInfoDto(date, false, 0, isHoliday, false);
            } else {
                dateInfoDto = new DateInfoDto(date, dateInfo.get().getMembers().contains(stdNum), dateInfo.get().getMembers().size(), isHoliday, dateInfo.get().getIsAble());
            }
            dateInfoArrayList.add(dateInfoDto);
        }

        return dateInfoArrayList;
    }

    @Transactional
    public Boolean apply(LocalDate date, String stdNum, MemberRole role) {
        Optional<DateEntity> targetDate = dateRepository.findById(date);
        Optional<MemberEntity> targetMember = memberRepository.findByStudentNumber(stdNum);

        DateEntity dateEntity = new DateEntity();

        if (targetMember.isEmpty()) {
            return false;
        }

        if (targetDate.isEmpty()) {
            return false;
        }

        targetDate.get().getMembers().add(stdNum);
//        열쇠 담당이 신청할 경우 isAble을 true로 변경
        if (role.equals(MemberRole.Key)) {
            targetDate.get().setIsAble(true);
        }

        dateRepository.save(targetDate.get());

        return true;
    }

    @Transactional
    public Boolean cancel(LocalDate date, String stdNum, MemberRole role) {
        Optional<DateEntity> targetDate = dateRepository.findById(date);
        Optional<MemberEntity> targetMember = memberRepository.findByStudentNumber(stdNum);

        if (targetMember.isEmpty()) {
            return false;
        }

        if (targetDate.isEmpty()) {
            return false;
        }

        targetDate.get().getMembers().remove(stdNum);
//        열쇠 담당이 취소할 경우 다른 열쇠 담당이 있는지 확인
        if (role.equals(MemberRole.Key)) {
            Set<String> members = targetDate.get().getMembers();
            boolean keyExist = false;
            for (String memberNum : members) {
                Optional<MemberEntity> byStudentNumber = memberRepository.findByStudentNumber(memberNum);
                MemberRole memberRole = byStudentNumber.get().getRole();
                if (memberRole.equals(MemberRole.Key)) {
                    keyExist = true;
                    break;
                }
            }
            targetDate.get().setIsAble(keyExist);
        }

        dateRepository.save(targetDate.get());


        return true;
    }

    public List<UserListDto> getUserList(String date) {

        LocalDate localDate = LocalDate.parse(date);
        Optional<DateEntity> byId = dateRepository.findById(localDate);

        List<UserListDto> list = new ArrayList<>();

        if (byId.isEmpty()) {
            return list;
        }

        Set<String> members = byId.get().getMembers();

        for (String member : members) {
            UserListDto user = new UserListDto();

            Optional<MemberEntity> byStudentNumber = memberRepository.findByStudentNumber(member);

            if (byStudentNumber.isEmpty()) {
                continue;
            }

            String name = byStudentNumber.get().getName();

            user.setStudentNumber(member);
            user.setName(name);

            list.add(user);
        }

        return list;

    }

}
