package com.arka.MSOrden.application.dtos;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonPropertyOrder({
        "idOrden",
        "idUsuario",
        "nombreUsuario",
        "fechaCreacion",
        "fechaUltimoMovimiento",
        "totalPrecio",
        "totalProductos",
        "totalUnidades",
        "estadoOrden"
})
public class ListarOrdenDto {
    private Long idOrden;
    private Long idUsuario;
    private String nombreUsuario;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaUltimoMovimiento;
    private Double totalPrecio;
    private Long totalProductos;
    private Long totalUnidades;
    private String estadoOrden;
}

