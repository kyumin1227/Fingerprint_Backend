package com.example.fingerprint_backend.filter;

import com.example.fingerprint_backend.entity.MemberEntity;
import com.example.fingerprint_backend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String StudentNumber) throws UsernameNotFoundException {

        MemberEntity memberEntity = memberRepository.getByStudentNumber(StudentNumber);
        if (memberEntity != null) {
            return new CustomUserDetails(memberEntity);
        }
        return null;
    }
}
