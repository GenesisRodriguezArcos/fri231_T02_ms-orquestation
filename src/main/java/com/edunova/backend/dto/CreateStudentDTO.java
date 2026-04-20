package com.edunova.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateStudentDTO {
    private String code;
    private String dni;
    private String firstName;
    private String lastName;
    private String motherLastName;
    private String email;
    private String phone;
    private String grade;
    private String section;
}
