package com.edunova.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarningDTO {
    private Long id;
    private Long studentId;
    private String studentName;
    private LocalDateTime date;
    private String type;
    private String typeDescription;
    private String reason;
}
