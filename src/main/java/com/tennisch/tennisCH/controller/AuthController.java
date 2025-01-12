package com.tennisch.tennisCH.controller;

import com.tennisch.tennisCH.model.Role;
import com.tennisch.tennisCH.model.User;
import com.tennisch.tennisCH.security.JwtUtil;
import com.tennisch.tennisCH.service.UserService;
import lombok.Data;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final BCryptPasswordEncoder bcryptPasswordEncoder;

    @Autowired
    public AuthController(JwtUtil jwtUtil, UserService userService, BCryptPasswordEncoder bcryptPasswordEncoder) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.bcryptPasswordEncoder = bcryptPasswordEncoder;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void createInitialUser() {
        if (userService.getAllUsers().isEmpty()) {
            User user = new User();
            user.setUsername("raul");
            user.setPassword("123");
            user.setEmail("raul@example.com");
            user.setFirstName("Raul");
            user.setLastName("Admin");
            user.setRegistrationDate(LocalDateTime.now());
            user.setRole(Role.ADMIN);
            userService.createUser(user);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        User user = userService.getAllUsers().stream()
                .filter(u -> u.getUsername().equals(loginRequest.getUsername()))
                .findFirst()
                .orElse(null);

        if (user != null && bcryptPasswordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            String token = jwtUtil.generateToken(user.getUsername());
            return ResponseEntity.ok(new LoginResponse(token));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Credentials");
        }
    }
}

@Data
class LoginRequest {
    private String username;
    private String password;
}

@Getter
class LoginResponse {
    private final String token;

    public LoginResponse(String token) {
        this.token = token;
    }
}