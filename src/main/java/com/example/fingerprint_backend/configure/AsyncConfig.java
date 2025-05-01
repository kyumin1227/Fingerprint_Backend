package com.example.fingerprint_backend.configure;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
@Profile({"dev", "prod", "!test"})
public class AsyncConfig implements AsyncConfigurer {
}
