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
@Table("students")
public class Student {
    
    @Id
    private Long id;
    
    private String code;
    
    private String dni;
    
    private String firstName;
    
    private String lastName;
    
    private String motherLastName;
    
    private String email;
    
    private String phone;
    
    private String grade;  // '1','2','3','4','5'
    
    private String section;
    
    private String status;  // 'A','I','S'
    
    private LocalDateTime registrationDate;
}
