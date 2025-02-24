package com.example.fingerprint_backend;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

@Configuration
@ActiveProfiles("test")
public class DotenvTestInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext context) {
        Dotenv dotenv = Dotenv.load();

        System.setProperty("GOOGLE_CLIENT_ID", dotenv.get("GOOGLE_CLIENT_ID"));
        System.setProperty("KAKAO_CLIENT_ID", dotenv.get("KAKAO_CLIENT_ID"));
        System.setProperty("ROLE_ADMIN", dotenv.get("ROLE_ADMIN"));
        System.setProperty("ROLE_PROFESSOR", dotenv.get("ROLE_PROFESSOR"));
        System.setProperty("ROLE_ASSISTANT", dotenv.get("ROLE_ASSISTANT"));
        System.setProperty("ROLE_KEY", dotenv.get("ROLE_KEY"));
        System.setProperty("ROLE_STUDENT", dotenv.get("ROLE_STUDENT"));
        System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));
        System.setProperty("JWT_EXPIRATION", dotenv.get("JWT_EXPIRATION"));
    }

}
