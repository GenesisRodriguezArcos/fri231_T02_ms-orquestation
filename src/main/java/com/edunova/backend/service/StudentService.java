package com.edunova.backend.service;

import com.edunova.backend.model.Student;
import com.edunova.backend.model.Tardiness;
import com.edunova.backend.model.Warning;
import com.edunova.backend.repository.StudentRepository;
import com.edunova.backend.repository.TardinessRepository;
import com.edunova.backend.repository.WarningRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StudentService {
    
    private final StudentRepository studentRepository;
    private final TardinessRepository tardinessRepository;
    private final WarningRepository warningRepository;
    
    // ========== STUDENTS ==========
    public Flux<Student> findAll() {
        return studentRepository.findAllByOrderById();
    }
    
    public Mono<Student> findById(Long id) {
        return studentRepository.findById(id);
    }
    
    public Mono<Student> save(Student student) {
        return studentRepository.save(student);
    }
    
    public Mono<Student> updateStatus(Long id, String status) {
        return studentRepository.findById(id)
            .flatMap(student -> {
                student.setStatus(status);
                return studentRepository.save(student);
            });
    }
    
    public Flux<Student> findByStatus(String status) {
        return studentRepository.findByStatusOrderById(status);
    }
    
    // ========== TARDINESS ==========
    public Mono<Tardiness> registerTardiness(Long studentId, Integer minutes, String reason, Boolean justified) {
        Tardiness tardiness = new Tardiness();
        tardiness.setStudentId(studentId);
        tardiness.setDate(LocalDateTime.now());
        tardiness.setArrivalTime(LocalTime.now());
        tardiness.setMinutesLate(minutes);
        tardiness.setReason(reason);
        tardiness.setJustified(justified != null ? justified : false);
        tardiness.setRegisteredBy("SYSTEM");
        
        return tardinessRepository.save(tardiness);
    }
    
    public Flux<Tardiness> getTardinessByStudent(Long studentId) {
        return tardinessRepository.findByStudentIdOrderByDateDesc(studentId);
    }
    
    public Flux<Tardiness> getAllTardiness() {
        return tardinessRepository.findAllByOrderByDateDesc();
    }
    
    // ========== WARNINGS ==========
    public Mono<Warning> registerWarning(Long studentId, String type, String reason) {
        Warning warning = new Warning();
        warning.setStudentId(studentId);
        warning.setDate(LocalDateTime.now());
        warning.setType(type);
        warning.setReason(reason);
        warning.setRegisteredBy("SYSTEM");
        
        return warningRepository.save(warning);
    }
    
    public Flux<Warning> getWarningsByStudent(Long studentId) {
        return warningRepository.findByStudentIdOrderByDateDesc(studentId);
    }
    
    public Flux<Warning> getAllWarnings() {
        return warningRepository.findAllByOrderByDateDesc();
    }
    
    // ========== REPORTS ==========
    public Mono<Map<String, Object>> getCompleteReport(Long studentId) {
        return studentRepository.findById(studentId)
            .flatMap(student -> 
                tardinessRepository.findByStudentIdOrderByDateDesc(studentId)
                    .collectList()
                    .zipWith(warningRepository.findByStudentIdOrderByDateDesc(studentId).collectList())
                    .map(tuple -> {
                        Map<String, Object> report = new HashMap<>();
                        report.put("student", student);
                        report.put("total_tardiness", tuple.getT1().size());
                        report.put("tardiness_records", tuple.getT1());
                        report.put("total_warnings", tuple.getT2().size());
                        report.put("warning_records", tuple.getT2());
                        return report;
                    })
            );
    }
    
    public Mono<Map<String, Object>> getStatistics() {
        return studentRepository.count()
            .zipWith(tardinessRepository.count())
            .zipWith(warningRepository.count())
            .map(tuple -> {
                Map<String, Object> stats = new HashMap<>();
                stats.put("total_students", tuple.getT1().getT1());
                stats.put("total_tardiness", tuple.getT1().getT2());
                stats.put("total_warnings", tuple.getT2());
                return stats;
            });
    }
}
