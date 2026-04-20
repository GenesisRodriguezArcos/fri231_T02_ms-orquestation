package com.edunova.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsDTO {
    private Long totalStudents;
    private Long totalTardiness;
    private Long totalWarnings;
    private Long activeStudents;
    private Long inactiveStudents;
    private Long suspendedStudents;
}
