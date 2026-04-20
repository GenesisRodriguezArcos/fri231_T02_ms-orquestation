package com.edunova.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("tardiness")
public class Tardiness {
    
    @Id
    private Long id;
    
    private Long studentId;
    
    private LocalDateTime date;
    
    private LocalTime arrivalTime;
    
    private Integer minutesLate;
    
    private Boolean justified;
    
    private String reason;
    
    private String registeredBy;
}
