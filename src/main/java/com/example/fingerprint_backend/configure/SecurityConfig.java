package com.example.fingerprint_backend.configure;

import com.example.fingerprint_backend.jwt.CustomUserDetailService;
import com.example.fingerprint_backend.jwt.JwtAuthenticationFilter;
import com.example.fingerprint_backend.jwt.JWTUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JWTUtil JWTUtil;
    private final CustomUserDetailService userDetailsService;

    public SecurityConfig(JWTUtil JWTUtil, CustomUserDetailService userDetailsService) {
        this.JWTUtil = JWTUtil;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        JwtAuthenticationFilter jwtFilter = new JwtAuthenticationFilter(JWTUtil, userDetailsService);

        http
                .csrf(csrf -> csrf.disable())
                .formLogin(formLogin -> formLogin.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .sessionManagement((session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)))
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/login").permitAll()
                        .requestMatchers("/api/fingerprint/**").permitAll()
                        .requestMatchers("/api/clean/manager/**").hasRole("MANAGER")
                        .requestMatchers("/api/admin/**").hasAnyRole("PROFESSOR", "ASSISTANT")
                        .anyRequest().permitAll()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
