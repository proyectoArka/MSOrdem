package com.arka.MSOrden.infrastructure.exception;

import com.arka.MSOrden.application.dto.ErrorResponseDto;
import com.arka.MSOrden.domain.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * Manejador global de excepciones para el microservicio.
 * Captura todas las excepciones y las convierte en respuestas HTTP apropiadas.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ==================== Excepciones de Dominio (404 - NOT FOUND) ====================

    @ExceptionHandler(OrdenNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleOrdenNotFound(
            OrdenNotFoundException ex, ServerWebExchange exchange) {
        logger.warn("Orden no encontrada: {}", ex.getMessage());

        ErrorResponseDto error = ErrorResponseDto.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .build();

        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(error));
    }

    @ExceptionHandler(ProductoNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleProductoNotFound(
            ProductoNotFoundException ex, ServerWebExchange exchange) {
        logger.warn("Producto no encontrado: {}", ex.getMessage());

        ErrorResponseDto error = ErrorResponseDto.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .build();

        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(error));
    }

    @ExceptionHandler(UsuarioNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleUsuarioNotFound(
            UsuarioNotFoundException ex, ServerWebExchange exchange) {
        logger.warn("Usuario no encontrado: {}", ex.getMessage());

        ErrorResponseDto error = ErrorResponseDto.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .build();

        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(error));
    }

    @ExceptionHandler(EstadoOrdenNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleEstadoOrdenNotFound(
            EstadoOrdenNotFoundException ex, ServerWebExchange exchange) {
        logger.warn("Estado de orden no encontrado: {}", ex.getMessage());

        ErrorResponseDto error = ErrorResponseDto.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .build();

        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(error));
    }

    // ==================== Excepciones de Dominio (400 - BAD REQUEST) ====================

    @ExceptionHandler(StockInsuficienteException.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleStockInsuficiente(
            StockInsuficienteException ex, ServerWebExchange exchange) {
        logger.warn("Stock insuficiente: {}", ex.getMessage());

        ErrorResponseDto error = ErrorResponseDto.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .build();

        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error));
    }

    @ExceptionHandler(InvalidProductQuantityException.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleInvalidProductQuantity(
            InvalidProductQuantityException ex, ServerWebExchange exchange) {
        logger.warn("Cantidad de producto inválida: {}", ex.getMessage());

        ErrorResponseDto error = ErrorResponseDto.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .build();

        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error));
    }

    @ExceptionHandler(EmptyProductListException.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleEmptyProductList(
            EmptyProductListException ex, ServerWebExchange exchange) {
        logger.warn("Lista de productos vacía: {}", ex.getMessage());

        ErrorResponseDto error = ErrorResponseDto.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .build();

        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error));
    }

    @ExceptionHandler(IllegalStateException.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleIllegalState(
            IllegalStateException ex, ServerWebExchange exchange) {
        logger.warn("Estado ilegal: {}", ex.getMessage());

        ErrorResponseDto error = ErrorResponseDto.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .build();

        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error));
    }

    // ==================== Excepciones de Validación ====================

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleValidationErrors(
            WebExchangeBindException ex, ServerWebExchange exchange) {
        logger.warn("Error de validación: {}", ex.getMessage());

        List<String> details = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                details.add(error.getField() + ": " + error.getDefaultMessage())
        );

        ErrorResponseDto error = ErrorResponseDto.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Error de validación: " + String.join(", ", details))
                .build();

        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error));
    }

    // ==================== Excepciones de Spring ====================

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleIllegalArgument(
            IllegalArgumentException ex, ServerWebExchange exchange) {
        logger.warn("Argumento ilegal: {}", ex.getMessage());

        ErrorResponseDto error = ErrorResponseDto.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .build();

        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleResponseStatus(
            ResponseStatusException ex, ServerWebExchange exchange) {
        logger.warn("ResponseStatusException: {} - {}", ex.getStatusCode(), ex.getReason());

        ErrorResponseDto error = ErrorResponseDto.builder()
                .status(ex.getStatusCode().value())
                .message(ex.getReason() != null ? ex.getReason() : "Error en la solicitud")
                .build();

        return Mono.just(ResponseEntity.status(ex.getStatusCode()).body(error));
    }

    // ==================== Excepciones Genéricas ====================

    @ExceptionHandler(DomainException.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleDomainException(
            DomainException ex, ServerWebExchange exchange) {
        logger.error("Error de dominio: {}", ex.getMessage(), ex);

        ErrorResponseDto error = ErrorResponseDto.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .build();

        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleGenericException(
            Exception ex, ServerWebExchange exchange) {
        logger.error("Error inesperado: {}", ex.getMessage(), ex);

        ErrorResponseDto error = ErrorResponseDto.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("Ha ocurrido un error interno en el servidor")
                .build();

        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error));
    }
}

