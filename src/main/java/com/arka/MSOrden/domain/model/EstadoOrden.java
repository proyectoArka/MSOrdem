package com.arka.MSOrden.domain.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EstadoOrden {
    private Long id;
    private String nombreEstado;
}
