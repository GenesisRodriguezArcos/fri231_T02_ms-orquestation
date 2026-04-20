package com.edunova.backend.controller;

import com.edunova.backend.dto.*;
import com.edunova.backend.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class StudentController {
    
    private final StudentService service;
    
    // ========== STUDENT ENDPOINTS ==========
    @GetMapping("/students")
    public Flux<StudentDTO> getAllStudents() {
        return service.findAllDTO();
    }
    
    @GetMapping("/students/{id}")
    public Mono<StudentDTO> getStudentById(@PathVariable Long id) {
        return service.findDTOById(id);
    }
    
    @PostMapping("/students")
    public Mono<StudentDTO> createStudent(@RequestBody CreateStudentDTO dto) {
        return service.createStudent(dto);
    }
    
    // ========== TARDINESS ENDPOINTS ==========
    @PostMapping("/students/{id}/tardiness")
    public Mono<TardinessDTO> registerTardiness(
            @PathVariable Long id,
            @RequestBody RegisterTardinessDTO dto) {
        return service.registerTardiness(id, dto);
    }
    
    @GetMapping("/students/{id}/tardiness")
    public Flux<TardinessDTO> getTardinessByStudent(@PathVariable Long id) {
        return service.getTardinessDTOByStudent(id);
    }
    
    @GetMapping("/tardiness")
    public Flux<TardinessDTO> getAllTardiness() {
        return service.getAllTardinessDTO();
    }
    
    // ========== WARNING ENDPOINTS ==========
    @PostMapping("/students/{id}/warning")
    public Mono<WarningDTO> registerWarning(
            @PathVariable Long id,
            @RequestBody RegisterWarningDTO dto) {
        return service.registerWarning(id, dto);
    }
    
    @GetMapping("/students/{id}/warnings")
    public Flux<WarningDTO> getWarningsByStudent(@PathVariable Long id) {
        return service.getWarningsDTOByStudent(id);
    }
    
    @GetMapping("/warnings")
    public Flux<WarningDTO> getAllWarnings() {
        return service.getAllWarningsDTO();
    }
    
    // ========== REPORT ENDPOINTS ==========
    @GetMapping("/students/{id}/report")
    public Mono<ReportDTO> getCompleteReport(@PathVariable Long id) {
        return service.getCompleteReport(id);
    }
    
    @GetMapping("/statistics")
    public Mono<StatisticsDTO> getStatistics() {
        return service.getStatisticsDTO();
    }
    
    @GetMapping("/health")
    public Mono<ResponseDTO<String>> health() {
        return Mono.just(ResponseDTO.success("API funcionando correctamente"));
    }
}
