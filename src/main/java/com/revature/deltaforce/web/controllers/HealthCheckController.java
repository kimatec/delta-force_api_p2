package com.revature.deltaforce.web.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthCheckController {

    @GetMapping(produces = "application/json")
    public String health() {
        return "{\"status\": \"UP\"}";
    }
}
