package com.arka.MSOrden.application.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecibirNewOrdenDto {

    @JsonProperty("idUsuario")
    private Long idUsuario;

    @JsonProperty("productos")
    private List<RecibirNewOrdenProductosDto> productos;
}
