package com.arka.MSOrden.application.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleProductoOrdenDto {
    private Long idProducto;
    private String nombreProducto;
    private String descripcionProducto;
    private Integer cantidad;
    private Double precioUnidad;
    private Double precioTotal;
}

