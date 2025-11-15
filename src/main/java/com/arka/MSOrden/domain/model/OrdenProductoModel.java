package com.arka.MSOrden.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdenProductoModel {
    private Long id;
    private Long ordenId;
    private Long productoId;
    private Long cantidad;
    private Double precioTotal;
}