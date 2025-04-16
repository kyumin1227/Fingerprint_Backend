package com.example.fingerprint_backend.jwt;

import com.example.fingerprint_backend.service.AccountService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JWTUtil {

    private final AccountService accountService;
    private static final Logger log = LoggerFactory.getLogger(JWTUtil.class);

    public JWTUtil(AccountService accountService) {
        this.accountService = accountService;
    }

    @Value("${custom.jwt.secret}")
    private String JWT_SECRET;

    // JWT 만료 시간 (밀리초 단위)
    @Value("${custom.jwt.expiration}")
    private long JWT_EXPIRATION;

    public String generateToken(String studentNumber, String email) {

        return jwtBuild(studentNumber, email, JWT_EXPIRATION);
    }

    public String generateToken(String studentNumber, String email, long customJwtExpirationInMs) {
        if (customJwtExpirationInMs <= 0) {
            throw new IllegalArgumentException("만료 시간은 0보다 커야 합니다.");
        }
        return jwtBuild(studentNumber, email, customJwtExpirationInMs);
    }

    public String jwtBuild(String studentNumber, String email, long JWT_EXPIRATION) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION);
        Key key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes());

        return Jwts.builder()
                .setSubject(studentNumber)  // 토큰의 주체(subject)에 학번을 설정
                .claim("email", email)      // 추가 클레임: 이메일
                .claim("role", accountService.getRole(studentNumber))   // 추가 클레임: 역할
                .claim("class", accountService.getClassId(studentNumber)) // 추가 클레임: 반
                .setIssuedAt(now)           // 발행 시간
                .setExpiration(expiryDate)  // 만료 시간
                .signWith(key, SignatureAlgorithm.HS256) // HS256 알고리즘으로 서명
                .compact();
    }

    public boolean validateToken(String token) {
        Key key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes());

        try {
            Jwts.parser()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.warn("만료된 JWT 토큰입니다.");
            return false;
        } catch (UnsupportedJwtException e) {
            log.warn("지원되지 않는 JWT 토큰입니다.");
            return false;
        } catch (MalformedJwtException e) {
            log.warn("잘못된 JWT 토큰입니다.");
            return false;
        } catch (SignatureException e) {
            log.warn("JWT 서명 검증에 실패했습니다.");
            return false;
        } catch (IllegalArgumentException e) {
            log.warn("JWT 토큰이 비어있습니다.");
            return false;
        }

        return true;
    }

    public String getStudentNumberFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(JWT_SECRET.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Long getClassIdFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(JWT_SECRET.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("class", Long.class);
    }
}
