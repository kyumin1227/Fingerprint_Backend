package com.example.fingerprint_backend;

import com.example.fingerprint_backend.service.JwtService;
import com.example.fingerprint_backend.types.MemberRole;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(initializers = DotenvTestInitializer.class)
@Transactional
public class JwtTest {

    @Autowired
    JwtService jwtService = new JwtService();

    @Test
    void createJwt() {

        String token = jwtService.generateToken("2423002", "kyumin1227@naver.com");
        System.out.println(token);
    }
}
