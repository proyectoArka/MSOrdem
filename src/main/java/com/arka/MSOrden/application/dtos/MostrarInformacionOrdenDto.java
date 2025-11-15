package com.arka.MSOrden.application.dtos;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonPropertyOrder({
        "idOrden",
        "idUsuario",
        "nombreUsuario",
        "direccionEnvio",
        "emailUsuario",
        "numeroContacto",
        "estadoOrden",
        "totalOrden",
        "numeroProductos",
        "cantidadTotalProductos",
        "fechaCreacion",
        "productos"
})
public class MostrarInformacionOrdenDto {
    private Long idOrden;
    private Long idUsuario;
    private String nombreUsuario;
    private String direccionEnvio;
    private String emailUsuario;
    private String numeroContacto;
    private String estadoOrden;
    private Double totalOrden;
    private Long numeroProductos;
    private Long cantidadTotalProductos;
    private LocalDateTime fechaCreacion;
    private List<MostrarInformacionProductosDto> productos;
}
