package com.edunova.backend.exception;

import com.edunova.backend.dto.ApiResponseDTO;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Mono<ApiResponseDTO<Void>> handleResourceNotFound(ResourceNotFoundException ex) {
        return Mono.just(ApiResponseDTO.error(ex.getMessage(), "RESOURCE_NOT_FOUND"));
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<ApiResponseDTO<Void>> handleBadRequest(BadRequestException ex) {
        return Mono.just(ApiResponseDTO.error(ex.getMessage(), "BAD_REQUEST"));
    }

    @ExceptionHandler(DuplicateResourceException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Mono<ApiResponseDTO<Void>> handleDuplicateResource(DuplicateResourceException ex) {
        return Mono.just(ApiResponseDTO.error(ex.getMessage(), "DUPLICATE_RESOURCE"));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Mono<ApiResponseDTO<Void>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String message = "Violacion de integridad de datos. Verifique que los datos sean unicos y validos.";
        return Mono.just(ApiResponseDTO.error(message, "DATA_INTEGRITY_VIOLATION", ex.getMostSpecificCause().getMessage()));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Mono<ApiResponseDTO<Void>> handleGenericException(Exception ex) {
        return Mono.just(ApiResponseDTO.error("Error interno del servidor", "INTERNAL_ERROR", ex.getMessage()));
    }
}
