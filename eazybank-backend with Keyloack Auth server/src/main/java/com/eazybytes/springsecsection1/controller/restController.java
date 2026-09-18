package com.eazybytes.springsecsection1.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class restController {

    @GetMapping("/")
    public String showDetails()
    {
        return "Welcome to the world of Spring Security";
    }
}
