package com.glub.aural_middleware.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VoiceController {
    @GetMapping("/test")
    public String testEndpoint() {
        return "API is working";
    }
}
