package com.arka.MSOrden.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información de un producto a agregar a la orden")
public class RecibirNewOrdenProductosDto {

    @JsonProperty("productoId")
    @Schema(description = "ID del producto en el inventario", example = "1", required = true)
    private Long productoId;

    @JsonProperty("cantidad")
    @Schema(description = "Cantidad de unidades del producto", example = "5", required = true, minimum = "1")
    private Long cantidad;
}
