package com.edunova.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponseDTO<T> {
    private boolean success;
    private String message;
    private String code;
    private LocalDateTime timestamp;
    private T data;
    private String errorDetails;
    
    public static <T> ApiResponseDTO<T> success(T data, String message) {
        ApiResponseDTO<T> response = new ApiResponseDTO<>();
        response.setSuccess(true);
        response.setMessage(message);
        response.setCode("SUCCESS");
        response.setTimestamp(LocalDateTime.now());
        response.setData(data);
        return response;
    }
    
    public static <T> ApiResponseDTO<T> success(String message) {
        ApiResponseDTO<T> response = new ApiResponseDTO<>();
        response.setSuccess(true);
        response.setMessage(message);
        response.setCode("SUCCESS");
        response.setTimestamp(LocalDateTime.now());
        return response;
    }
    
    public static <T> ApiResponseDTO<T> error(String message, String code) {
        ApiResponseDTO<T> response = new ApiResponseDTO<>();
        response.setSuccess(false);
        response.setMessage(message);
        response.setCode(code);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }
    
    public static <T> ApiResponseDTO<T> error(String message, String code, String errorDetails) {
        ApiResponseDTO<T> response = new ApiResponseDTO<>();
        response.setSuccess(false);
        response.setMessage(message);
        response.setCode(code);
        response.setTimestamp(LocalDateTime.now());
        response.setErrorDetails(errorDetails);
        return response;
    }
}
