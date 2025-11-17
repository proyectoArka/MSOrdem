package com.arka.MSOrden.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para respuestas de error estandarizadas.
 * Solo contiene status y message para respuestas simples y limpias.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta de error estandarizada")
public class ErrorResponseDto {

    @Schema(description = "Código de estado HTTP", example = "400")
    private int status;

    @Schema(description = "Mensaje descriptivo del error", example = "Stock insuficiente para el producto ID 1")
    private String message;
}
