package com.arka.MSOrden.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdenModel {
    private Long id;
    private Long userId;
    private LocalDateTime creationOrden;
    private LocalDateTime fechaUltimaModificacion;
    private Double totalPedido;
    private Long totalDeProductos;
    private Long totalUnidades;
    private Long estadoorden;
}
