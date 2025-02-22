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
    @GetMapping("/test-hash")
    public ResponseEntity<?> testHash() {
        try {
            String rawPassword = "123";
            String hashedPassword = bcryptPasswordEncoder.encode(rawPassword);
            return ResponseEntity.ok("Hashed Password: " + hashedPassword);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred: " + e.getMessage());
        }
    }



    @GetMapping("/test-login")
    public ResponseEntity<?> testLogin() {
        String rawPassword = "123";
        String hashedPassword = "$2a$10$DhssNGtIwGgmO/XvAsfXOenM5d/uEm6ib2qCSJ5yj3J3/N4HGfhZ6"; // Your database hash
        boolean matches = bcryptPasswordEncoder.matches(rawPassword, hashedPassword);

        System.out.println("Raw Password: " + rawPassword);
        System.out.println("Hashed Password: " + hashedPassword);
        System.out.println("Matches: " + matches);

        return ResponseEntity.ok("Password match result: " + matches);
    }

    @PostMapping("/debug-token")
    public ResponseEntity<?> generateAdminToken(@RequestParam(defaultValue = "admin") String username) {
        User user = userService.getUserByUsername(username);
        if (user != null && user.getRole() == Role.ADMIN) {
            String token = jwtUtil.generateToken(user.getUsername());
            return ResponseEntity.ok(new LoginResponse(token));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Admin user not found");
    }

    @EventListener(ApplicationReadyEvent.class)
    public void createInitialUser() {
        if (userService.getAllUsers().isEmpty()) {
            User user = new User();
            user.setUsername("admin");
            user.setPassword(bcryptPasswordEncoder.encode("123"));
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
        System.out.println("Login request received for username: " + loginRequest.getUsername());

        User user = userService.getUserByUsername(loginRequest.getUsername());

        if (user != null) {
            System.out.println("User found: " + user.getUsername());
            System.out.println("Encoded password in DB: " + user.getPassword());
            System.out.println("Password provided: " + loginRequest.getPassword());

            // Test password matching logic
            boolean passwordMatches = bcryptPasswordEncoder.matches(loginRequest.getPassword(), user.getPassword());
            System.out.println("Password matches: " + passwordMatches);

            if (passwordMatches) {
                String token = jwtUtil.generateToken(user.getUsername());
                System.out.println("Password matches, token generated: " + token);
                return ResponseEntity.ok(new LoginResponse(token));
            } else {
                System.out.println("Password does not match for user: " + loginRequest.getUsername());
            }
        } else {
            System.out.println("User not found: " + loginRequest.getUsername());
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Credentials");
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