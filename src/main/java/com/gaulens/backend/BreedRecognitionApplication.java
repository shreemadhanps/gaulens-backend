package com.gaulens.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the GauLens backend.
 * Run with: mvn spring-boot:run
 * API base URL: http://localhost:8080/api
 * H2 console:   http://localhost:8080/h2-console
 */
@SpringBootApplication
public class BreedRecognitionApplication {

    public static void main(String[] args) {
        SpringApplication.run(BreedRecognitionApplication.class, args);
    }

}
