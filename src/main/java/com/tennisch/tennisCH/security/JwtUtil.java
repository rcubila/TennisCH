package com.tennisch.tennisCH.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.antlr.v4.runtime.Token;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;


@Component
public class JwtUtil {
    //Replace the SECRET_KEY with your own secure key
    //Store the secret key in a secure configuration (like environment variables or a secure vault)
    private static final String SECRET_KEY = "your_very_long_and_secure_secret_key_at_least_32_characters";
    private static final long JWT_TOKEN_VALIDITY = 60 * 60 * 1000; // 1 hour in milliseconds
    private final SecretKey key;

    public JwtUtil(){
        this.key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String userName){
        Map<String, Object> claims = new HashMap<>();
        claims.put("userName", userName);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userName)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims extractClaims(String token){
        try{
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            throw new RuntimeException("Invalid JWT token: " + e.getMessage());
        }
    }

    public String extractUsername(String token){
        try{
            return extractClaims(token).get("username", String.class);
        } catch (Exception e){
            return  null;
        }
    }

    public boolean isTokenExpired(String token){
        try {
            return extractClaims(token).getExpiration().before(new Date());
        } catch (JwtException e) {
            return true;
        }
    }

    public boolean validateToken(String token, String username){
        try {
            final String tokenUserName = extractUsername(token);
            return (username.equals(tokenUserName) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }


    @PostConstruct
    public void testTokenGeneration() {
        String token = generateToken("testUser");
        System.out.println("Sample Token: " + token);
    }

}