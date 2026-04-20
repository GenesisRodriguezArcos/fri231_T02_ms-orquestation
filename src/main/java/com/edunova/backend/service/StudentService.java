package com.edunova.backend.service;

import com.edunova.backend.dto.*;
import com.edunova.backend.exception.*;
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
    
    // ========== STUDENT METHODS ==========
    public Flux<StudentDTO> findAllDTO() {
        return studentRepository.findAllByOrderById()
            .map(this::convertToDTO)
            .switchIfEmpty(Flux.error(new ResourceNotFoundException("Estudiante", "list", "no hay registros")));
    }
    
    public Mono<StudentDTO> findDTOById(Long id) {
        return studentRepository.findById(id)
            .map(this::convertToDTO)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("Estudiante", "id", id)));
    }
    
    public Mono<ApiResponseDTO<StudentDTO>> createStudent(CreateStudentDTO dto) {
        return validateUniqueFields(dto.getCode(), dto.getDni(), dto.getEmail())
            .then(Mono.defer(() -> {
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
                    .map(this::convertToDTO)
                    .map(saved -> ApiResponseDTO.success(saved, "Estudiante registrado exitosamente. Codigo: " + saved.getCode()));
            }));
    }
    
    public Mono<ApiResponseDTO<StudentDTO>> updateStudent(Long id, CreateStudentDTO dto) {
        return studentRepository.findById(id)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("Estudiante", "id", id)))
            .flatMap(student -> {
                student.setFirstName(dto.getFirstName());
                student.setLastName(dto.getLastName());
                student.setMotherLastName(dto.getMotherLastName());
                student.setPhone(dto.getPhone());
                student.setGrade(dto.getGrade());
                student.setSection(dto.getSection());
                return studentRepository.save(student);
            })
            .map(this::convertToDTO)
            .map(updated -> ApiResponseDTO.success(updated, "Estudiante actualizado correctamente. ID: " + id));
    }
    
    public Mono<ApiResponseDTO<Void>> deleteStudent(Long id) {
        return studentRepository.findById(id)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("Estudiante", "id", id)))
            .flatMap(student -> studentRepository.delete(student)
                .then(Mono.just(ApiResponseDTO.success("Estudiante eliminado exitosamente. ID: " + id))));
    }
    
    public Mono<ApiResponseDTO<StudentDTO>> updateStudentStatus(Long id, String status) {
        return studentRepository.findById(id)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("Estudiante", "id", id)))
            .flatMap(student -> {
                student.setStatus(status);
                return studentRepository.save(student);
            })
            .map(this::convertToDTO)
            .map(updated -> {
                String statusMessage = status.equals("A") ? "activado" : (status.equals("I") ? "inactivado" : "suspendido");
                return ApiResponseDTO.success(updated, "Estudiante " + statusMessage + " correctamente. ID: " + id);
            });
    }
    
    // ========== TARDINESS METHODS ==========
    public Mono<ApiResponseDTO<TardinessDTO>> registerTardiness(Long studentId, RegisterTardinessDTO dto) {
        return studentRepository.findById(studentId)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("Estudiante", "id", studentId)))
            .flatMap(student -> {
                Tardiness tardiness = new Tardiness();
                tardiness.setStudentId(studentId);
                tardiness.setDate(LocalDateTime.now());
                tardiness.setArrivalTime(LocalTime.now());
                tardiness.setMinutesLate(dto.getMinutes());
                tardiness.setReason(dto.getReason());
                tardiness.setJustified(dto.getJustified() != null ? dto.getJustified() : false);
                tardiness.setRegisteredBy("SYSTEM");
                
                return tardinessRepository.save(tardiness)
                    .map(saved -> {
                        TardinessDTO tardinessDTO = convertToTardinessDTO(saved, convertToDTO(student));
                        String message = String.format("Tardanza registrada. Estudiante: %s %s, Minutos: %d, Justificada: %s",
                            student.getFirstName(), student.getLastName(), dto.getMinutes(), 
                            (dto.getJustified() != null && dto.getJustified()) ? "Si" : "No");
                        return ApiResponseDTO.success(tardinessDTO, message);
                    });
            });
    }
    
    public Flux<TardinessDTO> getTardinessDTOByStudent(Long studentId) {
        return studentRepository.findById(studentId)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("Estudiante", "id", studentId)))
            .flatMapMany(student -> tardinessRepository.findByStudentIdOrderByDateDesc(studentId)
                .map(tardiness -> convertToTardinessDTO(tardiness, convertToDTO(student))))
            .switchIfEmpty(Flux.error(new ResourceNotFoundException("Tardanza", "estudianteId", studentId)));
    }
    
    // ========== WARNING METHODS ==========
    public Mono<ApiResponseDTO<WarningDTO>> registerWarning(Long studentId, RegisterWarningDTO dto) {
        return studentRepository.findById(studentId)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("Estudiante", "id", studentId)))
            .flatMap(student -> {
                Warning warning = new Warning();
                warning.setStudentId(studentId);
                warning.setDate(LocalDateTime.now());
                warning.setType(dto.getType());
                warning.setReason(dto.getReason());
                warning.setRegisteredBy("SYSTEM");
                
                return warningRepository.save(warning)
                    .map(saved -> {
                        WarningDTO warningDTO = convertToWarningDTO(saved, convertToDTO(student));
                        String typeDescription = getTypeDescription(dto.getType());
                        String message = String.format("Llamado de atencion registrado. Estudiante: %s %s, Tipo: %s, Motivo: %s",
                            student.getFirstName(), student.getLastName(), typeDescription, dto.getReason());
                        return ApiResponseDTO.success(warningDTO, message);
                    });
            });
    }
    
    // ========== REPORT METHODS ==========
    public Mono<ApiResponseDTO<ReportDTO>> getCompleteReport(Long studentId) {
        return studentRepository.findById(studentId)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("Estudiante", "id", studentId)))
            .flatMap(student -> {
                StudentDTO studentDTO = convertToDTO(student);
                return tardinessRepository.findByStudentIdOrderByDateDesc(studentId)
                    .collectList()
                    .zipWith(warningRepository.findByStudentIdOrderByDateDesc(studentId).collectList())
                    .map(tuple -> {
                        ReportDTO report = new ReportDTO();
                        report.setStudent(studentDTO);
                        report.setTotalTardiness(tuple.getT1().size());
                        report.setTardinessRecords(tuple.getT1().stream()
                            .map(t -> convertToTardinessDTO(t, studentDTO)).toList());
                        report.setTotalWarnings(tuple.getT2().size());
                        report.setWarningRecords(tuple.getT2().stream()
                            .map(w -> convertToWarningDTO(w, studentDTO)).toList());
                        return report;
                    })
                    .map(report -> ApiResponseDTO.success(report, "Reporte generado exitosamente para el estudiante: " + studentDTO.getFirstName() + " " + studentDTO.getLastName()));
            });
    }
    
    public Mono<ApiResponseDTO<StatisticsDTO>> getStatisticsDTO() {
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
                stats.setSuspendedStudents(tuple.getT1().getT1().getT1().getT1() - tuple.getT1().getT2() - tuple.getT2());
                return stats;
            })
            .map(stats -> ApiResponseDTO.success(stats, "Estadisticas generales del sistema"));
    }
    
    // ========== VALIDATION METHODS ==========
    private Mono<Void> validateUniqueFields(String code, String dni, String email) {
        return studentRepository.findByCode(code)
            .flatMap(existing -> Mono.<Void>error(new DuplicateResourceException("El codigo " + code + " ya esta registrado")))
            .switchIfEmpty(Mono.defer(() -> 
                studentRepository.findByDni(dni)
                    .flatMap(existing -> Mono.<Void>error(new DuplicateResourceException("El DNI " + dni + " ya esta registrado")))
                    .switchIfEmpty(Mono.defer(() ->
                        studentRepository.findByEmail(email)
                            .flatMap(existing -> Mono.<Void>error(new DuplicateResourceException("El email " + email + " ya esta registrado")))
                            .then(Mono.empty())
                    ))
            )).then();
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
