package com.tennisch.tennisCH.config;

import com.tennisch.tennisCH.payload.request.JwtRequestFilter;
import com.tennisch.tennisCH.security.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil();
    }

    @Bean
    public JwtRequestFilter jwtRequestFilter(JwtUtil jwtUtil) {
        return new JwtRequestFilter(jwtUtil);
    }
}