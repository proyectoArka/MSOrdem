package com.arka.MSOrden.application.dtos;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdenNotificacion {
    private String nombreCliente;
    private String emailCliente;
    private String estadoOrden;
    private Long numeroOrden;
    private Double totalOrden;
    private String urlSeguimiento;
    private List<ProductoOrdenNotificacion> productos;
}

