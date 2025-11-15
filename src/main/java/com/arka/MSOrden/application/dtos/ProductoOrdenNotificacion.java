package com.arka.MSOrden.application.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoOrdenNotificacion {
    private String nombreProducto;
    private Integer cantidad;
    private Double precioUnitario;
    private Double precioTotal;
}

