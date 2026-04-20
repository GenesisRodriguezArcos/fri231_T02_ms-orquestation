package com.edunova.backend.controller;

import com.edunova.backend.model.Student;
import com.edunova.backend.model.Tardiness;
import com.edunova.backend.model.Warning;
import com.edunova.backend.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class StudentController {
    
    private final StudentService service;
    
    // ========== STUDENT ENDPOINTS ==========
    @GetMapping("/students")
    public Flux<Student> getAllStudents() {
        return service.findAll();
    }
    
    @GetMapping("/students/{id}")
    public Mono<Student> getStudentById(@PathVariable Long id) {
        return service.findById(id);
    }
    
    @PostMapping("/students")
    public Mono<Student> createStudent(@RequestBody Student student) {
        return service.save(student);
    }
    
    @PutMapping("/students/{id}/status")
    public Mono<Student> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return service.updateStatus(id, body.get("status"));
    }
    
    @GetMapping("/students/status/{status}")
    public Flux<Student> getStudentsByStatus(@PathVariable String status) {
        return service.findByStatus(status);
    }
    
    // ========== TARDINESS ENDPOINTS ==========
    @PostMapping("/students/{id}/tardiness")
    public Mono<Tardiness> registerTardiness(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        Integer minutes = (Integer) body.get("minutes");
        String reason = (String) body.get("reason");
        Boolean justified = (Boolean) body.get("justified");
        return service.registerTardiness(id, minutes, reason, justified);
    }
    
    @GetMapping("/students/{id}/tardiness")
    public Flux<Tardiness> getTardinessByStudent(@PathVariable Long id) {
        return service.getTardinessByStudent(id);
    }
    
    @GetMapping("/tardiness")
    public Flux<Tardiness> getAllTardiness() {
        return service.getAllTardiness();
    }
    
    // ========== WARNING ENDPOINTS ==========
    @PostMapping("/students/{id}/warning")
    public Mono<Warning> registerWarning(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String type = body.get("type");
        String reason = body.get("reason");
        return service.registerWarning(id, type, reason);
    }
    
    @GetMapping("/students/{id}/warnings")
    public Flux<Warning> getWarningsByStudent(@PathVariable Long id) {
        return service.getWarningsByStudent(id);
    }
    
    @GetMapping("/warnings")
    public Flux<Warning> getAllWarnings() {
        return service.getAllWarnings();
    }
    
    // ========== REPORT ENDPOINTS ==========
    @GetMapping("/students/{id}/report")
    public Mono<Map<String, Object>> getCompleteReport(@PathVariable Long id) {
        return service.getCompleteReport(id);
    }
    
    @GetMapping("/statistics")
    public Mono<Map<String, Object>> getStatistics() {
        return service.getStatistics();
    }
    
    @GetMapping("/health")
    public Mono<Map<String, String>> health() {
        return Mono.just(Map.of(
            "status", "OK", 
            "service", "edunova-backend-reactive",
            "database", "PostgreSQL",
            "tables", "students, tardiness, warnings"
        ));
    }
}
