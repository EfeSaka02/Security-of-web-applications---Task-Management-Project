package com.example.task_management.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello") // That's test endpoint it uses HTTP method
    public String hello() {
        return "Hello, user!";
    }
}