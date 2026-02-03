package com.example.task_management.controller;

import com.example.task_management.dto.CreateUserRequest;
import com.example.task_management.model.User;
import com.example.task_management.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // Constructor
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/test") // Test endpoint accessible via token
    public String test() {
        return "User API is working!";
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody CreateUserRequest request) {
        try {
            User user = userService.createUser(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}