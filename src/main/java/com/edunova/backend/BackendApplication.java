package com.edunova.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
        System.out.println("🚀 Edunova Reactive Backend running on http://localhost:8080");
        System.out.println("📊 Available endpoints:");
        System.out.println("   GET  /api/students");
        System.out.println("   GET  /api/tardiness");
        System.out.println("   GET  /api/warnings");
        System.out.println("   POST /api/students/{id}/tardiness");
        System.out.println("   POST /api/students/{id}/warning");
        System.out.println("   GET  /api/students/{id}/report");
        System.out.println("   GET  /api/statistics");
        System.out.println("   GET  /api/health");
    }
}
