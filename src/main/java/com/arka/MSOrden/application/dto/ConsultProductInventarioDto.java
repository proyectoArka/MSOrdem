package com.arka.MSOrden.application.dto;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConsultProductInventarioDto {
    private String descripcion;
    private Long stock;
    private Double price;
    private String nombre;
}
