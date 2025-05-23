package com.fiitimprove.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Start point of the application
 */
@SpringBootApplication
@EnableScheduling
public class FitImproveApplication {
    public static void main(String[] args) {
        SpringApplication.run(FitImproveApplication.class, args);
    }
}
