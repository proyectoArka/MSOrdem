package com.arka.MSOrden.application.dtos;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultUserInAuthDto {
    private String name;
    private String email;
    private String direccion;
    private String telefono;
}
