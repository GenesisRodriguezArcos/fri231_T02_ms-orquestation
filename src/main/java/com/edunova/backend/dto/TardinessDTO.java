package com.edunova.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TardinessDTO {
    private Long id;
    private Long studentId;
    private String studentName;
    private LocalDateTime date;
    private LocalTime arrivalTime;
    private Integer minutesLate;
    private Boolean justified;
    private String reason;
}
