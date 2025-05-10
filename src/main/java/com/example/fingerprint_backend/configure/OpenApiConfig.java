package com.example.fingerprint_backend.configure;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Bannote API")
                        .description("Bannote 백엔드 API 문서")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Bannote Team")
                                .url("https://github.com/Bannote")
                                .email("kyumin12271227@gmail.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}