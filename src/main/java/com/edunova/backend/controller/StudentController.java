package com.edunova.backend.controller;

import com.edunova.backend.dto.*;
import com.edunova.backend.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
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
    public Mono<ApiResponseDTO<StudentDTO>> createStudent(@RequestBody CreateStudentDTO dto) {
        return service.createStudent(dto);
    }
    
    @PutMapping("/students/{id}")
    public Mono<ApiResponseDTO<StudentDTO>> updateStudent(@PathVariable Long id, @RequestBody CreateStudentDTO dto) {
        return service.updateStudent(id, dto);
    }
    
    @DeleteMapping("/students/{id}")
    public Mono<ApiResponseDTO<Void>> deleteStudent(@PathVariable Long id) {
        return service.deleteStudent(id);
    }
    
    @PatchMapping("/students/{id}/status")
    public Mono<ApiResponseDTO<StudentDTO>> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return service.updateStudentStatus(id, status);
    }
    
    // ========== TARDINESS ENDPOINTS ==========
    @PostMapping("/students/{id}/tardiness")
    public Mono<ApiResponseDTO<TardinessDTO>> registerTardiness(
            @PathVariable Long id,
            @RequestBody RegisterTardinessDTO dto) {
        return service.registerTardiness(id, dto);
    }
    
    @GetMapping("/students/{id}/tardiness")
    public Flux<TardinessDTO> getTardinessByStudent(@PathVariable Long id) {
        return service.getTardinessDTOByStudent(id);
    }
    
    // ========== WARNING ENDPOINTS ==========
    @PostMapping("/students/{id}/warning")
    public Mono<ApiResponseDTO<WarningDTO>> registerWarning(
            @PathVariable Long id,
            @RequestBody RegisterWarningDTO dto) {
        return service.registerWarning(id, dto);
    }
    
    // ========== REPORT ENDPOINTS ==========
    @GetMapping("/students/{id}/report")
    public Mono<ApiResponseDTO<ReportDTO>> getCompleteReport(@PathVariable Long id) {
        return service.getCompleteReport(id);
    }
    
    @GetMapping("/statistics")
    public Mono<ApiResponseDTO<StatisticsDTO>> getStatistics() {
        return service.getStatisticsDTO();
    }
    
    @GetMapping("/health")
    public Mono<ApiResponseDTO<String>> health() {
        return Mono.just(ApiResponseDTO.success("API REST funcionando correctamente. Sistema de control de tardanzas operativo."));
    }
}
