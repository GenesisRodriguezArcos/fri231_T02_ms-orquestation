package com.edunova.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterWarningDTO {
    private String type;  // L, G, M
    private String reason;
}
