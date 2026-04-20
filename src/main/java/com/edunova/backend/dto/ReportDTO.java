package com.edunova.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportDTO {
    private StudentDTO student;
    private Integer totalTardiness;
    private List<TardinessDTO> tardinessRecords;
    private Integer totalWarnings;
    private List<WarningDTO> warningRecords;
}
