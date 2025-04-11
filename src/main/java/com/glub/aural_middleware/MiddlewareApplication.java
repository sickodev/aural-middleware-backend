package com.glub.aural_middleware;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@Slf4j
@SpringBootApplication
public class MiddlewareApplication {

    @Autowired
    private Environment env;

    public static void main(String[] args) {
        SpringApplication.run(MiddlewareApplication.class, args);
    }

    @PostConstruct
    public void logActiveProfile() {
        String[] profiles = env.getActiveProfiles();
        System.out.println("✅ Active Spring profile: " + (profiles.length > 0 ? String.join(", ", profiles) : "none"));
    }
}
