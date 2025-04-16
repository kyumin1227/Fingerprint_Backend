package com.example.fingerprint_backend;

import com.example.fingerprint_backend.jwt.JWTUtil;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Transactional
public class JwtTest {

    @Autowired
    private JWTUtil JWTUtil;

//    @Test
//    void createJwt() {
//
//        String token = JWTUtil.generateToken("2423002", "kyumin1227@naver.com");
//        System.out.println(token);
//    }
}
