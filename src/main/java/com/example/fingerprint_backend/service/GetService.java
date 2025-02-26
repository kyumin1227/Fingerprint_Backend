package com.example.fingerprint_backend.service;

import com.example.fingerprint_backend.entity.MemberEntity;
import com.example.fingerprint_backend.repository.MemberRepository;
import org.springframework.stereotype.Service;

@Service
public class GetService {

    private final MemberRepository memberRepository;

    public GetService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /**
     * 학번으로 학생을 가져오는 함수
     * @param studentNumber
     * @return MemberEntity
     */
    public MemberEntity getMemberByStudentNumber(String studentNumber) {
        return memberRepository.findByStudentNumber(studentNumber).orElseThrow(
                () -> new IllegalArgumentException("해당 학번의 학생이 존재하지 않습니다.")
        );
    }
}
