package com.arka.MSOrden.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para crear o actualizar una orden con productos")
public class RecibirNewOrdenDto {

    @JsonProperty("idUsuario")
    @Schema(description = "ID del usuario que crea la orden", example = "1", required = true)
    private Long idUsuario;

    @JsonProperty("productos")
    @Schema(description = "Lista de productos a agregar a la orden", required = true)
    private List<RecibirNewOrdenProductosDto> productos;
}
