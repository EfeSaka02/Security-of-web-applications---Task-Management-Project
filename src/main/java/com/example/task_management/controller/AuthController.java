package com.example.task_management.controller;

import com.example.task_management.dto.AuthResponse;
import com.example.task_management.dto.CreateUserRequest;
import com.example.task_management.dto.LoginRequest;
import com.example.task_management.model.User;
import com.example.task_management.service.CustomUserDetailsService;
import com.example.task_management.service.UserService;
import com.example.task_management.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserService userService,
                          CustomUserDetailsService userDetailsService,
                          JwtUtil jwtUtil,
                          AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register") // That's the endpoint of user registration
    public ResponseEntity<?> register(@Valid @RequestBody CreateUserRequest request) { // That's for new registration We used Post method for adding data to database
        try {
            User user = userService.createUser(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new AuthResponse(null, user.getUsername(), "User registered successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );


            logger.info("Successful login for user: {}", request.getUsername());

            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
            String token = jwtUtil.generateToken(userDetails);

            return ResponseEntity.ok(new AuthResponse(token, userDetails.getUsername(), "Login successful"));

        } catch (BadCredentialsException e) {
            logger.warn("Failed login attempt for user: {}", request.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        } catch (Exception e) {
            logger.error("Login error for user: {}", request.getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }
}
