package com.edunova.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("warnings")
public class Warning {
    
    @Id
    private Long id;
    
    private Long studentId;
    
    private LocalDateTime date;
    
    private String type;  // 'L','G','M'
    
    private String reason;
    
    private String registeredBy;
}
