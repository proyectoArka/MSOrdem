package com.arka.MSOrden.application.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonPropertyOrder({
        "idOrden",
        "precioTotal",
        "cantidadProductos",
        "totalUnidades",
        "productos"
})
public class DetalleOrdenDto {
    private Long idOrden;
    private Double precioTotal;
    private Long cantidadProductos;
    private Long totalUnidades;
    private List<DetalleProductoOrdenDto> productos;
}

