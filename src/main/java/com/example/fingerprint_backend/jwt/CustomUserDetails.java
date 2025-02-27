package com.example.fingerprint_backend.jwt;

import com.example.fingerprint_backend.entity.MemberEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final MemberEntity memberEntity;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();

        memberEntity.getRoles().forEach(role -> {
            collection.add(
                    new GrantedAuthority() {
                @Override
                public String getAuthority() {
                    return "ROLE_" + role.toString();
                }
            });
        });

        return collection;
    }

    @Override
    public String getPassword() {
        return "password";
    }

    @Override
    public String getUsername() {
        System.out.println(memberEntity.getStudentNumber());
        return memberEntity.getStudentNumber();
    }

    public Long getClassId() {
        return memberEntity.getSchoolClass().getId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
