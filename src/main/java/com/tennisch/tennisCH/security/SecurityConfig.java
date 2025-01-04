package com.tennisch.tennisCH.security;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Temporarily disable security to troubleshoot 403 Forbidden
        http.csrf().disable() // Disable CSRF protection
                .authorizeRequests()
                .anyRequest().permitAll(); // Allow all requests to bypass security
        return http.build();
    }

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http.csrf().disable() // Disable CSRF for simplicity; enable in production with proper config
//                .authorizeRequests()
//                .requestMatchers("/api/auth/**").permitAll() // Allow unauthenticated access to auth endpoints
//                .requestMatchers("/api/users/**").permitAll() // Allow unauthenticated access to user creation
//                .anyRequest().authenticated() // All other requests need authentication
//                .and()
//                .formLogin().disable() // Disable default form login (optional)
//                .httpBasic().disable(); // Disable basic authentication (optional)
//        return http.build();
//    }

    @PostConstruct
    public void logSecurityContext() {
        System.out.println("Security context initialized: " + SecurityContextHolder.getContext());
    }



}
