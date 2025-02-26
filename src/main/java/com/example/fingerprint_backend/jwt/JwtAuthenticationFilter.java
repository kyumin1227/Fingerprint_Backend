package com.example.fingerprint_backend.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JWTUtil JWTUtil;
    private final CustomUserDetailService userDetailsService;

    public JwtAuthenticationFilter(JWTUtil JWTUtil, CustomUserDetailService userDetailsService) {
        this.JWTUtil = JWTUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        // Authorization 헤더에서 Bearer 토큰 추출
        String header = request.getHeader("Authorization");
        String token = null;
        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);
        }

        // 토큰이 존재하고, 아직 인증 정보가 없는 경우 검증
        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                if (JWTUtil.validateToken(token)) {
                    String studentNumber = JWTUtil.getStudentNumberFromToken(token);
                    CustomUserDetails userDetails = userDetailsService.loadUserByUsername(studentNumber);
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    System.out.println(userDetails.getUsername());
                    System.out.println(userDetails.getPassword());
                    System.out.println(userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    System.out.println("토큰 검증 성공");
                }
            } catch (Exception e) {
                logger.error("JWT 인증 필터에서 오류 발생: " + e.getClass() + e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}