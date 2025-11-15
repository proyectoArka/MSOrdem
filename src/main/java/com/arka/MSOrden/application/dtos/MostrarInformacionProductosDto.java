package com.arka.MSOrden.application.dtos;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MostrarInformacionProductosDto {
    private Long idProducto;
    private String nombreProducto;
    private String descripcionProducto;
    private Double precioUnitario;
    private Double precioTotal;
    private Integer cantidad;
}
