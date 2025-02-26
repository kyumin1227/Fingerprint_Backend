package com.example.fingerprint_backend.jwt;

import com.example.fingerprint_backend.service.AccountService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JWTUtil {

    private final AccountService roleService;

    public JWTUtil(AccountService roleService) {
        this.roleService = roleService;
    }

    @Value("${JWT_SECRET}")
    private String jwtSecret;

    // JWT 만료 시간 (밀리초 단위)
    @Value("${JWT_EXPIRATION}")
    private long jwtExpirationInMs;

    public String generateToken(String studentNumber, String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        // 비밀 키 생성. 실제 운영에서는 키 관리에 신경쓰셔야 합니다.
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        return Jwts.builder()
                .setSubject(studentNumber)  // 토큰의 주체(subject)에 학번을 설정
                .claim("email", email)      // 추가 클레임: 이메일
                .claim("role", roleService.getRole(studentNumber))   // 추가 클레임: 역할
                .setIssuedAt(now)           // 발행 시간
                .setExpiration(expiryDate)  // 만료 시간
                .signWith(key, SignatureAlgorithm.HS256) // HS256 알고리즘으로 서명
                .compact();
    }

    public boolean validateToken(String token) {
        return true;
    }

    public String getStudentNumberFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
