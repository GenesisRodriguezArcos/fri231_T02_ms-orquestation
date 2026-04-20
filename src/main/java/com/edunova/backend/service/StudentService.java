package com.edunova.backend.service;

import com.edunova.backend.dto.*;
import com.edunova.backend.model.*;
import com.edunova.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class StudentService {
    
    private final StudentRepository studentRepository;
    private final TardinessRepository tardinessRepository;
    private final WarningRepository warningRepository;
    
    // ========== STUDENT DTO METHODS ==========
    public Flux<StudentDTO> findAllDTO() {
        return studentRepository.findAllByOrderById()
            .map(this::convertToDTO);
    }
    
    public Mono<StudentDTO> findDTOById(Long id) {
        return studentRepository.findById(id)
            .map(this::convertToDTO);
    }
    
    public Mono<StudentDTO> createStudent(CreateStudentDTO dto) {
        Student student = new Student();
        student.setCode(dto.getCode());
        student.setDni(dto.getDni());
        student.setFirstName(dto.getFirstName());
        student.setLastName(dto.getLastName());
        student.setMotherLastName(dto.getMotherLastName());
        student.setEmail(dto.getEmail());
        student.setPhone(dto.getPhone());
        student.setGrade(dto.getGrade());
        student.setSection(dto.getSection());
        student.setStatus("A");
        student.setRegistrationDate(LocalDateTime.now());
        
        return studentRepository.save(student)
            .map(this::convertToDTO);
    }
    
    // ========== TARDINESS DTO METHODS ==========
    public Mono<TardinessDTO> registerTardiness(Long studentId, RegisterTardinessDTO dto) {
        Tardiness tardiness = new Tardiness();
        tardiness.setStudentId(studentId);
        tardiness.setDate(LocalDateTime.now());
        tardiness.setArrivalTime(LocalTime.now());
        tardiness.setMinutesLate(dto.getMinutes());
        tardiness.setReason(dto.getReason());
        tardiness.setJustified(dto.getJustified() != null ? dto.getJustified() : false);
        tardiness.setRegisteredBy("SYSTEM");
        
        return tardinessRepository.save(tardiness)
            .flatMap(saved -> findDTOById(studentId)
                .map(studentDTO -> convertToTardinessDTO(saved, studentDTO)));
    }
    
    public Flux<TardinessDTO> getTardinessDTOByStudent(Long studentId) {
        return findDTOById(studentId)
            .flatMapMany(studentDTO -> tardinessRepository.findByStudentIdOrderByDateDesc(studentId)
                .map(tardiness -> convertToTardinessDTO(tardiness, studentDTO)));
    }
    
    public Flux<TardinessDTO> getAllTardinessDTO() {
        return tardinessRepository.findAllByOrderByDateDesc()
            .flatMap(tardiness -> findDTOById(tardiness.getStudentId())
                .map(studentDTO -> convertToTardinessDTO(tardiness, studentDTO)));
    }
    
    // ========== WARNING DTO METHODS ==========
    public Mono<WarningDTO> registerWarning(Long studentId, RegisterWarningDTO dto) {
        Warning warning = new Warning();
        warning.setStudentId(studentId);
        warning.setDate(LocalDateTime.now());
        warning.setType(dto.getType());
        warning.setReason(dto.getReason());
        warning.setRegisteredBy("SYSTEM");
        
        return warningRepository.save(warning)
            .flatMap(saved -> findDTOById(studentId)
                .map(studentDTO -> convertToWarningDTO(saved, studentDTO)));
    }
    
    public Flux<WarningDTO> getWarningsDTOByStudent(Long studentId) {
        return findDTOById(studentId)
            .flatMapMany(studentDTO -> warningRepository.findByStudentIdOrderByDateDesc(studentId)
                .map(warning -> convertToWarningDTO(warning, studentDTO)));
    }
    
    public Flux<WarningDTO> getAllWarningsDTO() {
        return warningRepository.findAllByOrderByDateDesc()
            .flatMap(warning -> findDTOById(warning.getStudentId())
                .map(studentDTO -> convertToWarningDTO(warning, studentDTO)));
    }
    
    // ========== REPORT METHODS ==========
    public Mono<ReportDTO> getCompleteReport(Long studentId) {
        return findDTOById(studentId)
            .flatMap(studentDTO -> 
                tardinessRepository.findByStudentIdOrderByDateDesc(studentId)
                    .collectList()
                    .zipWith(warningRepository.findByStudentIdOrderByDateDesc(studentId).collectList())
                    .map(tuple -> {
                        ReportDTO report = new ReportDTO();
                        report.setStudent(studentDTO);
                        report.setTotalTardiness(tuple.getT1().size());
                        report.setTardinessRecords(tuple.getT1());
                        report.setTotalWarnings(tuple.getT2().size());
                        report.setWarningRecords(tuple.getT2());
                        return report;
                    })
            );
    }
    
    public Mono<StatisticsDTO> getStatisticsDTO() {
        return studentRepository.count()
            .zipWith(tardinessRepository.count())
            .zipWith(warningRepository.count())
            .zipWith(studentRepository.findByStatusOrderById("A").count())
            .zipWith(studentRepository.findByStatusOrderById("I").count())
            .map(tuple -> {
                StatisticsDTO stats = new StatisticsDTO();
                stats.setTotalStudents(tuple.getT1().getT1().getT1().getT1());
                stats.setTotalTardiness(tuple.getT1().getT1().getT1().getT2());
                stats.setTotalWarnings(tuple.getT1().getT1().getT2());
                stats.setActiveStudents(tuple.getT1().getT2());
                stats.setInactiveStudents(tuple.getT2());
                stats.setSuspendedStudents(0L);
                return stats;
            });
    }
    
    // ========== CONVERTERS ==========
    private StudentDTO convertToDTO(Student student) {
        return new StudentDTO(
            student.getId(), student.getCode(), student.getDni(),
            student.getFirstName(), student.getLastName(), student.getMotherLastName(),
            student.getEmail(), student.getPhone(), student.getGrade(),
            student.getSection(), student.getStatus()
        );
    }
    
    private TardinessDTO convertToTardinessDTO(Tardiness tardiness, StudentDTO student) {
        TardinessDTO dto = new TardinessDTO();
        dto.setId(tardiness.getId());
        dto.setStudentId(tardiness.getStudentId());
        dto.setStudentName(student.getFirstName() + " " + student.getLastName());
        dto.setDate(tardiness.getDate());
        dto.setArrivalTime(tardiness.getArrivalTime());
        dto.setMinutesLate(tardiness.getMinutesLate());
        dto.setJustified(tardiness.getJustified());
        dto.setReason(tardiness.getReason());
        return dto;
    }
    
    private WarningDTO convertToWarningDTO(Warning warning, StudentDTO student) {
        WarningDTO dto = new WarningDTO();
        dto.setId(warning.getId());
        dto.setStudentId(warning.getStudentId());
        dto.setStudentName(student.getFirstName() + " " + student.getLastName());
        dto.setDate(warning.getDate());
        dto.setType(warning.getType());
        dto.setTypeDescription(getTypeDescription(warning.getType()));
        dto.setReason(warning.getReason());
        return dto;
    }
    
    private String getTypeDescription(String type) {
        switch(type) {
            case "L": return "LEVE";
            case "G": return "GRAVE";
            case "M": return "MUY GRAVE";
            default: return "DESCONOCIDO";
        }
    }
}
