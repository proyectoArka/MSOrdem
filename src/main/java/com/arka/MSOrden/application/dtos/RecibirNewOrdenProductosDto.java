package com.arka.MSOrden.application.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecibirNewOrdenProductosDto {

    @JsonProperty("productoId")
    private Long productoId;

    @JsonProperty("cantidad")
    private Long cantidad;  // Mantenemos Long para compatibilidad interna
}
